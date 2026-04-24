package com.bezkoder.policylambda.config;

public final class AppConfig {

    private static final String DEFAULT_JWT_SECRET = "replace-this-jwt-secret-before-production-at-minimum-32-bytes";
    private static final long DEFAULT_JWT_EXPIRATION_MS = 86_400_000L;
    private static final String DEFAULT_ADMIN_USERNAME = "policyadmin";
    private static final String DEFAULT_ADMIN_EMAIL = "policyadmin@example.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "PolicyAdmin@123";

    private final String jdbcUrl;
    private final String databaseUsername;
    private final String databasePassword;
    private final String jwtSecret;
    private final long jwtExpirationMs;
    private final String defaultAdminUsername;
    private final String defaultAdminEmail;
    private final String defaultAdminPassword;

    private AppConfig(
            String jdbcUrl,
            String databaseUsername,
            String databasePassword,
            String jwtSecret,
            long jwtExpirationMs,
            String defaultAdminUsername,
            String defaultAdminEmail,
            String defaultAdminPassword) {
        this.jdbcUrl = jdbcUrl;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.defaultAdminUsername = defaultAdminUsername;
        this.defaultAdminEmail = defaultAdminEmail;
        this.defaultAdminPassword = defaultAdminPassword;
    }

    public static AppConfig fromEnvironment() {
        String jdbcUrl = readJdbcUrl();
        String dbUsername = required("DB_USERNAME");
        String dbPassword = required("DB_PASSWORD");
        String jwtSecret = env("JWT_SECRET", DEFAULT_JWT_SECRET);
        long jwtExpirationMs = parseLong(env("JWT_EXPIRATION_MS", String.valueOf(DEFAULT_JWT_EXPIRATION_MS)), DEFAULT_JWT_EXPIRATION_MS);

        return new AppConfig(
                jdbcUrl,
                dbUsername,
                dbPassword,
                jwtSecret,
                jwtExpirationMs,
                env("DEFAULT_ADMIN_USERNAME", DEFAULT_ADMIN_USERNAME),
                env("DEFAULT_ADMIN_EMAIL", DEFAULT_ADMIN_EMAIL),
                env("DEFAULT_ADMIN_PASSWORD", DEFAULT_ADMIN_PASSWORD)
        );
    }

    private static String readJdbcUrl() {
        String explicitJdbcUrl = env("JDBC_URL", null);
        if (hasText(explicitJdbcUrl)) {
            return explicitJdbcUrl;
        }

        String host = required("DB_HOST");
        String port = env("DB_PORT", "1433");
        String databaseName = required("DB_NAME");
        String encrypt = env("DB_ENCRYPT", "true");
        String trustServerCertificate = env("DB_TRUST_SERVER_CERTIFICATE", "true");

        return String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=%s;trustServerCertificate=%s;loginTimeout=30;",
                host,
                port,
                databaseName,
                encrypt,
                trustServerCertificate
        );
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (!hasText(value)) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value.trim();
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return hasText(value) ? value.trim() : defaultValue;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static long parseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    public String jdbcUrl() {
        return jdbcUrl;
    }

    public String databaseUsername() {
        return databaseUsername;
    }

    public String databasePassword() {
        return databasePassword;
    }

    public String jwtSecret() {
        return jwtSecret;
    }

    public long jwtExpirationMs() {
        return jwtExpirationMs;
    }

    public String defaultAdminUsername() {
        return defaultAdminUsername;
    }

    public String defaultAdminEmail() {
        return defaultAdminEmail;
    }

    public String defaultAdminPassword() {
        return defaultAdminPassword;
    }
}
