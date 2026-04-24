package com.bezkoder.policylambda.service;

import com.bezkoder.policylambda.db.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

public final class PolicyService {

    private static final Map<String, String> COLUMN_OVERRIDES = Map.of(
            "PRODUCTSTATUS", "policyStatus"
    );

    private final Database database;

    public PolicyService(Database database) {
        this.database = database;
    }

    public List<String> getPolicyTypes() {
        return readDistinctValues("""
                SELECT DISTINCT PRODUCT
                FROM POLICY
                WHERE PRODUCT IS NOT NULL
                  AND LTRIM(RTRIM(PRODUCT)) <> ''
                ORDER BY PRODUCT
                """);
    }

    public List<String> getPolicyStatuses(String policyType) {
        String normalizedPolicyType = requireText(policyType, "Policy type is required to load policy statuses");

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT DISTINCT PRODUCTSTATUS
                     FROM POLICY
                     WHERE PRODUCT = ?
                       AND PRODUCTSTATUS IS NOT NULL
                       AND LTRIM(RTRIM(PRODUCTSTATUS)) <> ''
                     ORDER BY PRODUCTSTATUS
                     """)) {
            statement.setString(1, normalizedPolicyType);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> statuses = new ArrayList<>();
                while (resultSet.next()) {
                    statuses.add(resultSet.getString(1));
                }
                return statuses;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load policy statuses", exception);
        }
    }

    public List<Map<String, Object>> searchPolicies(String policyType, String policyStatus) {
        String normalizedPolicyType = requireText(policyType, "Policy type is required to search policies");
        String normalizedPolicyStatus = normalize(policyStatus);

        StringBuilder sql = new StringBuilder("""
                SELECT id, POLICY_NUMBER, EXT_POLICY_NUMBER, PRODUCT, PRODUCTSTATUS, POL_STATUS_DESC,
                       TITLE, FORE_NAME, SUR_NAME, NI_NUMBER, POLICY_AGENCY, POL_START_DATE, RENEWAL_DATE
                FROM POLICY
                WHERE PRODUCT = ?
                """);

        if (normalizedPolicyStatus != null) {
            sql.append(" AND PRODUCTSTATUS = ?");
        }

        sql.append(" ORDER BY POLICY_NUMBER");

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setString(1, normalizedPolicyType);
            if (normalizedPolicyStatus != null) {
                statement.setString(2, normalizedPolicyStatus);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> policies = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, Object> policy = new LinkedHashMap<>();
                    policy.put("id", resultSet.getLong("id"));
                    policy.put("policyNumber", trimToNull(resultSet.getString("POLICY_NUMBER")));
                    policy.put("extPolicyNumber", trimToNull(resultSet.getString("EXT_POLICY_NUMBER")));
                    policy.put("policyType", trimToNull(resultSet.getString("PRODUCT")));
                    policy.put("policyStatus", trimToNull(resultSet.getString("PRODUCTSTATUS")));
                    policy.put("policyStatusDescription", trimToNull(resultSet.getString("POL_STATUS_DESC")));
                    policy.put("customerName", buildCustomerName(
                            resultSet.getString("TITLE"),
                            resultSet.getString("FORE_NAME"),
                            resultSet.getString("SUR_NAME")
                    ));
                    policy.put("niNumber", trimToNull(resultSet.getString("NI_NUMBER")));
                    policy.put("policyAgency", trimToNull(resultSet.getString("POLICY_AGENCY")));
                    policy.put("startDate", toIsoDate(resultSet.getDate("POL_START_DATE")));
                    policy.put("renewalDate", toIsoDate(resultSet.getDate("RENEWAL_DATE")));
                    policies.add(policy);
                }
                return policies;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to search policies", exception);
        }
    }

    public Map<String, Object> getPolicyById(long policyId) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM POLICY WHERE id = ?")) {
            statement.setLong(1, policyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new ApiException(404, "Policy not found with id: " + policyId);
                }

                Map<String, Object> policy = new LinkedHashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();

                for (int index = 1; index <= metaData.getColumnCount(); index++) {
                    String columnName = metaData.getColumnLabel(index);
                    String responseField = mapColumnName(columnName);
                    Object rawValue = resultSet.getObject(index);
                    policy.put(responseField, toSerializableValue(rawValue));
                }

                return policy;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load policy details", exception);
        }
    }

    private List<String> readDistinctValues(String sql) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<String> values = new ArrayList<>();
            while (resultSet.next()) {
                values.add(resultSet.getString(1));
            }
            return values;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load policy data", exception);
        }
    }

    private String requireText(String value, String message) {
        String normalizedValue = normalize(value);
        if (normalizedValue == null) {
            throw new ApiException(400, message);
        }
        return normalizedValue;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private String buildCustomerName(String title, String foreName, String surName) {
        StringJoiner joiner = new StringJoiner(" ");
        if (hasText(title)) {
            joiner.add(title.trim());
        }
        if (hasText(foreName)) {
            joiner.add(foreName.trim());
        }
        if (hasText(surName)) {
            joiner.add(surName.trim());
        }

        String customerName = joiner.toString();
        return customerName.isBlank() ? "Unknown policy holder" : customerName;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private String toIsoDate(Date date) {
        return date == null ? null : date.toLocalDate().toString();
    }

    private String mapColumnName(String columnName) {
        String override = COLUMN_OVERRIDES.get(columnName.toUpperCase(Locale.ROOT));
        if (override != null) {
            return override;
        }

        StringBuilder builder = new StringBuilder();
        boolean upperCaseNext = false;
        String normalizedName = columnName.toLowerCase(Locale.ROOT);

        for (char character : normalizedName.toCharArray()) {
            if (!Character.isLetterOrDigit(character)) {
                upperCaseNext = true;
                continue;
            }

            if (builder.isEmpty()) {
                builder.append(character);
                upperCaseNext = false;
                continue;
            }

            if (upperCaseNext) {
                builder.append(Character.toUpperCase(character));
                upperCaseNext = false;
            } else {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    private Object toSerializableValue(Object rawValue) {
        if (rawValue == null) {
            return null;
        }

        if (rawValue instanceof Date sqlDate) {
            return sqlDate.toLocalDate().toString();
        }

        if (rawValue instanceof LocalDate localDate) {
            return localDate.toString();
        }

        if (rawValue instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        if (rawValue instanceof LocalDateTime localDateTime) {
            return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        if (rawValue instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        if (rawValue instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        return rawValue;
    }
}
