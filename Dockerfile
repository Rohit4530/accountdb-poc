# Multi-stage Dockerfile for Spring Boot 4.0.5 + Java 21
# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

# Copy Maven wrapper and pom.xml first for better layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application
RUN ./mvnw clean package -DskipTests -B && \
    mkdir -p target/dependency && \
    (cd target/dependency; jar -xf ../*.jar)

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install required packages
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S policygroup && adduser -S policyuser -G policygroup

WORKDIR /app

# Copy dependency layer information from build stage
COPY --from=build /workspace/target/dependency/BOOT-INF/lib /app/lib
COPY --from=build /workspace/target/dependency/META-INF /app/META-INF
COPY --from=build /workspace/target/dependency/BOOT-INF/classes /app

# Change ownership to non-root user
RUN chown -R policyuser:policygroup /app

USER policyuser

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/test/all || exit 1

# JVM optimizations for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+OptimizeStringConcat \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.backgroundpreinitializer.ignore=true \
    -Dspring.jmx.enabled=false"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp /app:/app/lib/* com.bezkoder.springjwt.SpringBootSecurityJwtApplication"]
