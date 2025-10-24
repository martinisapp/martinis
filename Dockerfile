# ========================================
# Multi-Stage Build for Martinis Application
# ========================================

# ========================================
# Stage 1: Build Stage
# ========================================
FROM maven:3.9-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy POM file first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Verify the JAR was created successfully
RUN ls -la /app/target/ && \
    if [ ! -f /app/target/martinis.jar ]; then \
        echo "ERROR: JAR file was not created by Maven build!" && exit 1; \
    fi && \
    echo "SUCCESS: JAR file created at /app/target/martinis.jar"

# ========================================
# Stage 2: Runtime Stage
# ========================================
FROM eclipse-temurin:17-jre-jammy

# Install curl for healthchecks
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/martinis.jar ./martinis.jar

# Verify the JAR was copied successfully
RUN ls -la /app/ && \
    if [ ! -f /app/martinis.jar ]; then \
        echo "ERROR: JAR file was not copied to runtime stage!" && exit 1; \
    fi && \
    echo "SUCCESS: JAR file is available at /app/martinis.jar"

# Change ownership to appuser
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# JVM settings optimized for containers
ENV JAVA_OPTS="-Xmx768m -Xms256m \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=80.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=100 \
    -XX:+ParallelRefProcEnabled \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application
# Note: Using absolute path to ensure JAR can be found
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/martinis.jar"]
