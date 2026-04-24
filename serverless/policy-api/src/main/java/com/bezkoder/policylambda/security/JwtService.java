package com.bezkoder.policylambda.security;

import com.bezkoder.policylambda.model.UserRecord;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public final class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(String rawSecret, long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(toKeyBytes(rawSecret));
        this.expirationMs = expirationMs;
    }

    public String createToken(UserRecord user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(expirationMs);

        return Jwts.builder()
                .setSubject(user.username())
                .claim("userId", user.id())
                .claim("email", user.email())
                .claim("roles", user.roles())
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private byte[] toKeyBytes(String rawSecret) {
        String secret = rawSecret == null ? "" : rawSecret.trim();
        if (secret.isEmpty()) {
            throw new IllegalStateException("JWT secret must not be empty");
        }

        if (secret.matches("^[A-Za-z0-9+/=]+$") && secret.length() % 4 == 0) {
            try {
                byte[] decoded = Decoders.BASE64.decode(secret);
                if (decoded.length >= 32) {
                    return decoded;
                }
            } catch (IllegalArgumentException ignored) {
                // Fall back to UTF-8 bytes below.
            }
        }

        byte[] utf8Bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (utf8Bytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes long");
        }
        return utf8Bytes;
    }
}
