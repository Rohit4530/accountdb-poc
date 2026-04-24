package com.bezkoder.policylambda.db;

import com.bezkoder.policylambda.config.AppConfig;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SchemaInitializer {

    private static final Logger LOGGER = Logger.getLogger(SchemaInitializer.class.getName());
    private static final List<String> DEFAULT_ROLES = List.of("ROLE_USER", "ROLE_MODERATOR", "ROLE_ADMIN");

    private final Database database;
    private final AppConfig config;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public SchemaInitializer(Database database, AppConfig config) {
        this.database = database;
        this.config = config;
    }

    public void initialize() {
        if (initialized.get()) {
            return;
        }

        synchronized (this) {
            if (initialized.get()) {
                return;
            }

            try (Connection connection = database.getConnection()) {
                connection.setAutoCommit(false);
                createAuthTables(connection);

                for (String roleName : DEFAULT_ROLES) {
                    ensureRole(connection, roleName);
                }

                int adminRoleId = ensureRole(connection, "ROLE_ADMIN");
                long adminUserId = ensureDefaultAdminUser(connection);
                ensureUserRole(connection, adminUserId, adminRoleId);

                connection.commit();
                initialized.set(true);
                LOGGER.info("Policy API bootstrap completed successfully");
            } catch (SQLException exception) {
                throw new IllegalStateException("Failed to initialize database schema and seed data", exception);
            }
        }
    }

    private void createAuthTables(Connection connection) throws SQLException {
        execute(connection, """
                IF OBJECT_ID(N'roles', N'U') IS NULL
                BEGIN
                    CREATE TABLE roles (
                        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                        name VARCHAR(20) NOT NULL UNIQUE
                    );
                END;
                """);

        execute(connection, """
                IF OBJECT_ID(N'users', N'U') IS NULL
                BEGIN
                    CREATE TABLE users (
                        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                        username VARCHAR(20) NOT NULL UNIQUE,
                        email VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(120) NOT NULL
                    );
                END;
                """);

        execute(connection, """
                IF OBJECT_ID(N'user_roles', N'U') IS NULL
                BEGIN
                    CREATE TABLE user_roles (
                        user_id BIGINT NOT NULL,
                        role_id INT NOT NULL,
                        CONSTRAINT PK_user_roles PRIMARY KEY (user_id, role_id),
                        CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT FK_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
                    );
                END;
                """);
    }

    private int ensureRole(Connection connection, String roleName) throws SQLException {
        Integer existingRoleId = findRoleId(connection, roleName);
        if (existingRoleId != null) {
            return existingRoleId;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO roles(name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, roleName);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedRoleId = generatedKeys.getInt(1);
                    LOGGER.info("Seeded missing role: " + roleName);
                    return generatedRoleId;
                }
            }
        } catch (SQLException exception) {
            Integer roleIdAfterConflict = findRoleId(connection, roleName);
            if (roleIdAfterConflict != null) {
                return roleIdAfterConflict;
            }
            throw exception;
        }

        throw new SQLException("Role could not be created: " + roleName);
    }

    private Integer findRoleId(Connection connection, String roleName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM roles WHERE name = ?")) {
            statement.setString(1, roleName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        return null;
    }

    private long ensureDefaultAdminUser(Connection connection) throws SQLException {
        Long existingUserId = findUserId(connection, config.defaultAdminUsername(), config.defaultAdminEmail());
        if (existingUserId != null) {
            LOGGER.info("Default admin user already exists, ensuring admin role mapping");
            return existingUserId;
        }

        String passwordHash = BCrypt.hashpw(config.defaultAdminPassword(), BCrypt.gensalt());

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users(username, email, password) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, config.defaultAdminUsername());
            statement.setString(2, config.defaultAdminEmail());
            statement.setString(3, passwordHash);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long userId = generatedKeys.getLong(1);
                    LOGGER.info("Seeded default admin user: " + config.defaultAdminUsername());
                    return userId;
                }
            }
        } catch (SQLException exception) {
            Long userIdAfterConflict = findUserId(connection, config.defaultAdminUsername(), config.defaultAdminEmail());
            if (userIdAfterConflict != null) {
                return userIdAfterConflict;
            }

            LOGGER.log(Level.WARNING, "Admin user creation ran into a conflict", exception);
            throw exception;
        }

        throw new SQLException("Default admin user could not be created");
    }

    private Long findUserId(Connection connection, String username, String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT TOP 1 id FROM users WHERE username = ? OR email = ?")) {
            statement.setString(1, username);
            statement.setString(2, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                }
            }
        }

        return null;
    }

    private void ensureUserRole(Connection connection, long userId, int roleId) throws SQLException {
        try (PreparedStatement existsStatement = connection.prepareStatement(
                "SELECT 1 FROM user_roles WHERE user_id = ? AND role_id = ?")) {
            existsStatement.setLong(1, userId);
            existsStatement.setInt(2, roleId);

            try (ResultSet resultSet = existsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }
        }

        try (PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO user_roles(user_id, role_id) VALUES (?, ?)")) {
            insertStatement.setLong(1, userId);
            insertStatement.setInt(2, roleId);
            insertStatement.executeUpdate();
        }
    }

    private void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
