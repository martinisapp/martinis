# ========================================
# Optimized Multi-Stage Dockerfile for Railway.com
# ========================================
# This Dockerfile is specifically optimized for Railway deployment:
# - Fast builds with layer caching
# - Minimal runtime image size
# - Security best practices
# - Production-ready JVM settings
# ========================================

# ========================================
# Stage 1: Build Stage
# ========================================
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# Set working directory
WORKDIR /build

# Copy POM file first for better layer caching
# Dependencies only rebuild if pom.xml changes
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
# -DskipTests: Skip tests for faster builds (run tests in CI/CD)
# -B: Batch mode for cleaner logs
RUN mvn clean package -DskipTests -B

# Verify JAR was created
RUN test -f /build/target/martinis.jar || (echo "ERROR: JAR not created" && exit 1)

# ========================================
# Stage 2: Runtime Stage
# ========================================
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks (Railway uses this)
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder --chown=appuser:appuser /build/target/martinis.jar ./app.jar

# Switch to non-root user
USER appuser

# Expose port (Railway will override with $PORT)
EXPOSE 8080

# JVM settings optimized for Railway containers
# - UseContainerSupport: Respect container memory limits
# - MaxRAMPercentage: Use up to 75% of container memory
# - G1GC: Low-latency garbage collector
# - ExitOnOutOfMemoryError: Restart on OOM (Railway handles restart)
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=100 \
    -XX:+ParallelRefProcEnabled \
    -XX:+UseStringDeduplication \
    -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom"

# Health check for Railway
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Start the application
# Use shell form to allow environment variable expansion
CMD java $JAVA_OPTS -jar app.jar
