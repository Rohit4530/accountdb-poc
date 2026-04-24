package com.bezkoder.policylambda.db;

import com.bezkoder.policylambda.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {

    private final AppConfig config;

    public Database(AppConfig config) {
        this.config = config;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("SQL Server JDBC driver is not available", exception);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.jdbcUrl(),
                config.databaseUsername(),
                config.databasePassword()
        );
    }
}
