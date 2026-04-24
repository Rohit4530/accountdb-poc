package com.bezkoder.policylambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.bezkoder.policylambda.config.AppConfig;
import com.bezkoder.policylambda.db.Database;
import com.bezkoder.policylambda.db.SchemaInitializer;
import com.bezkoder.policylambda.security.JwtService;
import com.bezkoder.policylambda.service.ApiException;
import com.bezkoder.policylambda.service.AuthService;
import com.bezkoder.policylambda.service.PolicyService;
import com.bezkoder.policylambda.web.LoginRequest;
import com.bezkoder.policylambda.web.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PolicyApiHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Logger LOGGER = Logger.getLogger(PolicyApiHandler.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern POLICY_ID_PATH = Pattern.compile("^/api/policies/(\\d+)$");

    private final SchemaInitializer schemaInitializer;
    private final AuthService authService;
    private final PolicyService policyService;
    private final JwtService jwtService;

    public PolicyApiHandler() {
        AppConfig config = AppConfig.fromEnvironment();
        Database database = new Database(config);
        this.jwtService = new JwtService(config.jwtSecret(), config.jwtExpirationMs());
        this.schemaInitializer = new SchemaInitializer(database, config);
        this.authService = new AuthService(database, jwtService);
        this.policyService = new PolicyService(database);
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            schemaInitializer.initialize();
            return route(event);
        } catch (ApiException exception) {
            return jsonResponse(exception.statusCode(), Map.of("message", exception.getMessage()));
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Unhandled error in policy API Lambda", exception);
            return jsonResponse(500, Map.of("message", "Internal server error"));
        }
    }

    private APIGatewayV2HTTPResponse route(APIGatewayV2HTTPEvent event) throws IOException {
        String method = resolveMethod(event);
        String path = normalizePath(event == null ? null : event.getRawPath());

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return emptyResponse(204);
        }

        if ("GET".equals(method) && "/api/test/all".equals(path)) {
            return textResponse(200, "Public Content.");
        }

        if ("POST".equals(method) && "/api/auth/signin".equals(path)) {
            LoginRequest request = readBody(event, LoginRequest.class);
            return jsonResponse(200, authService.signIn(request));
        }

        if ("POST".equals(method) && "/api/auth/signup".equals(path)) {
            SignupRequest request = readBody(event, SignupRequest.class);
            return jsonResponse(200, authService.signUp(request));
        }

        authenticateRequest(event);

        if ("GET".equals(method) && "/api/policies/policy-types".equals(path)) {
            return jsonResponse(200, policyService.getPolicyTypes());
        }

        if ("GET".equals(method) && "/api/policies/policy-statuses".equals(path)) {
            String policyType = queryValue(event, "policyType");
            return jsonResponse(200, policyService.getPolicyStatuses(policyType));
        }

        if ("GET".equals(method) && "/api/policies/search".equals(path)) {
            String policyType = queryValue(event, "policyType");
            String policyStatus = queryValue(event, "policyStatus");
            return jsonResponse(200, policyService.searchPolicies(policyType, policyStatus));
        }

        Matcher policyIdMatcher = POLICY_ID_PATH.matcher(path);
        if ("GET".equals(method) && policyIdMatcher.matches()) {
            long policyId = Long.parseLong(policyIdMatcher.group(1));
            return jsonResponse(200, policyService.getPolicyById(policyId));
        }

        return jsonResponse(404, Map.of("message", "Route not found"));
    }

    private void authenticateRequest(APIGatewayV2HTTPEvent event) {
        String authorizationHeader = headerValue(event, "Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException(401, "Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            throw new ApiException(401, "Missing bearer token");
        }

        try {
            Claims claims = jwtService.parseToken(token);
            if (claims.getSubject() == null || claims.getSubject().isBlank()) {
                throw new ApiException(401, "Invalid JWT token");
            }
        } catch (JwtException exception) {
            throw new ApiException(401, "Invalid or expired JWT token");
        }
    }

    private String resolveMethod(APIGatewayV2HTTPEvent event) {
        if (event == null) {
            return "GET";
        }

        if (event.getRequestContext() != null
                && event.getRequestContext().getHttp() != null
                && event.getRequestContext().getHttp().getMethod() != null) {
            return event.getRequestContext().getHttp().getMethod().toUpperCase();
        }

        return "GET";
    }

    private String normalizePath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return "/";
        }

        String normalizedPath = rawPath.trim();
        if (normalizedPath.length() > 1 && normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath;
    }

    private <T> T readBody(APIGatewayV2HTTPEvent event, Class<T> bodyType) throws IOException {
        if (event == null || event.getBody() == null || event.getBody().isBlank()) {
            throw new ApiException(400, "Request body is required");
        }

        String body = event.getBody();
        if (Boolean.TRUE.equals(event.getIsBase64Encoded())) {
            body = new String(Base64.getDecoder().decode(body), StandardCharsets.UTF_8);
        }

        return OBJECT_MAPPER.readValue(body, bodyType);
    }

    private String queryValue(APIGatewayV2HTTPEvent event, String name) {
        if (event == null || event.getQueryStringParameters() == null) {
            return null;
        }
        return event.getQueryStringParameters().get(name);
    }

    private String headerValue(APIGatewayV2HTTPEvent event, String headerName) {
        if (event == null || event.getHeaders() == null || event.getHeaders().isEmpty()) {
            return null;
        }

        for (Map.Entry<String, String> header : event.getHeaders().entrySet()) {
            if (header.getKey() != null && header.getKey().equalsIgnoreCase(headerName)) {
                return header.getValue();
            }
        }

        return null;
    }

    private APIGatewayV2HTTPResponse jsonResponse(int statusCode, Object body) {
        return response(statusCode, "application/json", writeJson(body));
    }

    private APIGatewayV2HTTPResponse textResponse(int statusCode, String body) {
        return response(statusCode, "text/plain; charset=utf-8", body);
    }

    private APIGatewayV2HTTPResponse emptyResponse(int statusCode) {
        return response(statusCode, "application/json", "");
    }

    private APIGatewayV2HTTPResponse response(int statusCode, String contentType, String body) {
        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setStatusCode(statusCode);
        response.setIsBase64Encoded(false);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Authorization,Content-Type");
        headers.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        response.setHeaders(headers);
        response.setBody(body);
        return response;
    }

    private String writeJson(Object body) {
        try {
            return OBJECT_MAPPER.writeValueAsString(body);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to serialize response", exception);
        }
    }
}
