package com.bezkoder.policylambda.service;

import com.bezkoder.policylambda.db.Database;
import com.bezkoder.policylambda.model.UserRecord;
import com.bezkoder.policylambda.security.JwtService;
import com.bezkoder.policylambda.web.LoginRequest;
import com.bezkoder.policylambda.web.SignupRequest;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AuthService {

    private final Database database;
    private final JwtService jwtService;

    public AuthService(Database database, JwtService jwtService) {
        this.database = database;
        this.jwtService = jwtService;
    }

    public Map<String, Object> signIn(LoginRequest request) {
        if (request == null || !hasText(request.username()) || !hasText(request.password())) {
            throw new ApiException(400, "Username and password are required");
        }

        try (Connection connection = database.getConnection()) {
            UserRecord user = findUserByUsername(connection, request.username().trim());
            if (user == null || !BCrypt.checkpw(request.password(), user.passwordHash())) {
                throw new ApiException(401, "Invalid username or password");
            }

            String token = jwtService.createToken(user);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("accessToken", token);
            response.put("tokenType", "Bearer");
            response.put("id", user.id());
            response.put("username", user.username());
            response.put("email", user.email());
            response.put("roles", user.roles());
            return response;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to sign in user", exception);
        }
    }

    public Map<String, Object> signUp(SignupRequest request) {
        if (request == null) {
            throw new ApiException(400, "Signup payload is required");
        }

        validateSignupRequest(request);
        Set<String> roleNames = resolveRoleNames(request.role());

        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);

            if (existsByUsername(connection, request.username().trim())) {
                throw new ApiException(400, "Error: Username is already taken!");
            }

            if (existsByEmail(connection, request.email().trim())) {
                throw new ApiException(400, "Error: Email is already in use!");
            }

            long userId = insertUser(
                    connection,
                    request.username().trim(),
                    request.email().trim(),
                    BCrypt.hashpw(request.password(), BCrypt.gensalt())
            );

            Map<String, Integer> roleIds = loadRoleIds(connection, roleNames);
            for (Integer roleId : roleIds.values()) {
                insertUserRole(connection, userId, roleId);
            }

            connection.commit();
            return Map.of("message", "User registered successfully!");
        } catch (ApiException exception) {
            throw exception;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to register user", exception);
        }
    }

    private UserRecord findUserByUsername(Connection connection, String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT u.id, u.username, u.email, u.password, r.name AS role_name
                FROM users u
                LEFT JOIN user_roles ur ON ur.user_id = u.id
                LEFT JOIN roles r ON r.id = ur.role_id
                WHERE u.username = ?
                ORDER BY r.name
                """)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                Long id = null;
                String email = null;
                String passwordHash = null;
                LinkedHashSet<String> roles = new LinkedHashSet<>();

                while (resultSet.next()) {
                    if (id == null) {
                        id = resultSet.getLong("id");
                        email = resultSet.getString("email");
                        passwordHash = resultSet.getString("password");
                    }

                    String roleName = resultSet.getString("role_name");
                    if (hasText(roleName)) {
                        roles.add(roleName);
                    }
                }

                if (id == null) {
                    return null;
                }

                return new UserRecord(id, username, email, passwordHash, new ArrayList<>(roles));
            }
        }
    }

    private boolean existsByUsername(Connection connection, String username) throws SQLException {
        return exists(connection, "SELECT 1 FROM users WHERE username = ?", username);
    }

    private boolean existsByEmail(Connection connection, String email) throws SQLException {
        return exists(connection, "SELECT 1 FROM users WHERE email = ?", email);
    }

    private boolean exists(Connection connection, String sql, String value) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private long insertUser(Connection connection, String username, String email, String passwordHash) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users(username, email, password) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }

        throw new SQLException("User could not be created");
    }

    private Map<String, Integer> loadRoleIds(Connection connection, Set<String> roleNames) throws SQLException {
        Map<String, Integer> roleIds = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM roles WHERE name = ?")) {
            for (String roleName : roleNames) {
                statement.setString(1, roleName);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new ApiException(500, "Error: Role is not found.");
                    }

                    roleIds.put(roleName, resultSet.getInt("id"));
                }
            }
        }

        return roleIds;
    }

    private void insertUserRole(Connection connection, long userId, int roleId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO user_roles(user_id, role_id) VALUES (?, ?)")) {
            statement.setLong(1, userId);
            statement.setInt(2, roleId);
            statement.executeUpdate();
        }
    }

    private Set<String> resolveRoleNames(Set<String> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return Set.of("ROLE_USER");
        }

        LinkedHashSet<String> roleNames = new LinkedHashSet<>();
        for (String requestedRole : requestedRoles) {
            String normalizedRole = requestedRole == null ? "" : requestedRole.trim().toLowerCase();
            switch (normalizedRole) {
                case "admin", "role_admin" -> roleNames.add("ROLE_ADMIN");
                case "mod", "moderator", "role_moderator" -> roleNames.add("ROLE_MODERATOR");
                default -> roleNames.add("ROLE_USER");
            }
        }

        return roleNames;
    }

    private void validateSignupRequest(SignupRequest request) {
        String username = request.username() == null ? "" : request.username().trim();
        String email = request.email() == null ? "" : request.email().trim();
        String password = request.password() == null ? "" : request.password();

        if (username.length() < 3 || username.length() > 20) {
            throw new ApiException(400, "Username must be between 3 and 20 characters");
        }

        if (!email.contains("@") || email.length() > 50) {
            throw new ApiException(400, "A valid email address is required");
        }

        if (password.length() < 6 || password.length() > 40) {
            throw new ApiException(400, "Password must be between 6 and 40 characters");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
