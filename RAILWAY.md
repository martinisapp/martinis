# Railway.com Deployment Guide - Complete Rewrite

## Overview

This guide provides comprehensive instructions for deploying the Martinis screenplay management application on Railway.com. The application has been completely rewritten and optimized for Railway deployment with Spring Boot 3.2.0.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Quick Start](#quick-start)
3. [Detailed Deployment Steps](#detailed-deployment-steps)
4. [Environment Variables](#environment-variables)
5. [Database Setup](#database-setup)
6. [Configuration Files](#configuration-files)
7. [Monitoring & Troubleshooting](#monitoring--troubleshooting)
8. [Performance Optimization](#performance-optimization)

---

## Prerequisites

Before deploying to Railway, ensure you have:

- A Railway.com account (free tier available)
- Git repository with the Martinis application
- Basic understanding of Spring Boot applications
- Railway CLI (optional, for advanced usage)

## Quick Start

### Deploy from GitHub

The easiest deployment method is directly from this GitHub repository. See the [README.md](./README.md#quick-deploy-to-railway) for step-by-step instructions.

<!-- One-click template deploy coming soon -->
<!-- [![Deploy on Railway](https://railway.com/button.svg)](https://railway.com/new/template/YOUR-TEMPLATE-CODE) -->

### Manual Deployment

1. Visit [Railway.com](https://railway.com) and sign in
2. Click "New Project" → "Deploy from GitHub repo"
3. Select the `martinisapp/martinis` repository
4. Add MySQL database plugin
5. Configure environment variables
6. Deploy

---

## Detailed Deployment Steps

### Step 1: Create a New Railway Project

```bash
# Option 1: Using Railway CLI
railway login
railway init
railway link

# Option 2: Using Web Interface
# Visit https://railway.app/new
# Click "Deploy from GitHub repo"
```

### Step 2: Add MySQL Database

1. In your Railway project dashboard, click "New"
2. Select "Database" → "Add MySQL"
3. Railway will automatically create a MySQL instance
4. Copy the `DATABASE_URL` connection string

**Important:** Railway automatically sets the `DATABASE_URL` environment variable in the format:
```
mysql://user:password@host:port/database
```

### Step 3: Connect GitHub Repository

1. Click "New" → "GitHub Repo"
2. Select `martinisapp/martinis`
3. Choose the branch to deploy (default: main)
4. Railway will automatically detect the configuration files

### Step 4: Configure Environment Variables

Navigate to your service settings and add the following variables:

#### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | MySQL connection URL (auto-set by Railway) | `mysql://user:pass@host:3306/railway` |
| `PORT` | Application port (auto-set by Railway) | `8080` |

#### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `ADMIN_USERNAME` | Initial admin username | `admin` |
| `ADMIN_PASSWORD` | Initial admin password | None (must be set) |
| `ADMIN_FIRSTNAME` | Admin first name | `System` |
| `ADMIN_LASTNAME` | Admin last name | `Administrator` |

**Security Note:** Always set a strong `ADMIN_PASSWORD` on first deployment.

### Step 5: Deploy

```bash
# Using Railway CLI
railway up

# Or push to GitHub (auto-deploys)
git push origin main
```

Railway will:
1. Detect the Nixpacks configuration
2. Build the application using Maven
3. Create the `martinis.jar` artifact
4. Start the application with optimized JVM settings
5. Initialize the database schema automatically

---

## Environment Variables

### Database Configuration

The application uses Railway's `DATABASE_URL` environment variable, which is automatically set when you add a MySQL database to your project.

**Format:** `mysql://user:password@host:port/database`

The `DatabaseConfig.java` class automatically parses this URL and converts it to JDBC format with appropriate SSL settings.

### Admin User Bootstrap

Set these environment variables to create an initial admin user:

```bash
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your-secure-password-here
ADMIN_FIRSTNAME=John
ADMIN_LASTNAME=Doe
```

The password will be automatically hashed with BCrypt before storage.

### Port Configuration

Railway automatically assigns a port via the `PORT` environment variable. The application listens on this port.

**Default:** `8080` (used for local development)

---

## Database Setup

### Automatic Schema Initialization

The application automatically runs `schema.sql` on first startup to create all required tables:

- `user` - User accounts
- `authority` - User roles (ROLE_USER, ROLE_ADMIN)
- `project` - Screenplay projects
- `scene` - Scenes within projects
- `actor` - Actor database
- `person` - Characters in projects
- `block` - Screenplay blocks/lines

### Manual Database Access

```bash
# Using Railway CLI
railway connect mysql

# Or use the connection string from Railway dashboard
mysql -h host -u user -p database
```

### Database Migrations

The application uses Spring Boot's SQL initialization feature:

```properties
spring.sql.init.mode=always
```

To skip schema initialization after first deploy:
```bash
railway variables set SQL_INIT_MODE=never
```

---

## Configuration Files

This deployment uses the following configuration files:

### 1. `railway.json` - Railway Service Configuration

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "nixpacksConfigPath": "nixpacks.toml",
    "watchPatterns": ["src/**", "pom.xml"]
  },
  "deploy": {
    "startCommand": "java -Dserver.port=$PORT -Dspring.profiles.active=railway ...",
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10,
    "healthcheckPath": "/",
    "healthcheckTimeout": 100
  }
}
```

### 2. `nixpacks.toml` - Build Configuration

```toml
[phases.setup]
nixPkgs = ["maven_3_9", "jdk17_headless"]

[phases.build]
cmds = ["mvn clean package -DskipTests -B"]

[start]
cmd = "java -Dserver.port=$PORT ... -jar target/martinis.jar"
```

### 3. `Procfile` - Process Definition

```
web: java -Dserver.port=$PORT ... -jar target/martinis.jar
```

### 4. `app.json` - Application Manifest

Defines metadata, environment variables, and deployment settings.

---

## Monitoring & Troubleshooting

### View Logs

```bash
# Using Railway CLI
railway logs

# Or use the Railway Dashboard
# Navigate to your service → Deployments → View Logs
```

### Common Issues

#### 1. Database Connection Errors

**Symptom:** Application fails to start with database connection errors

**Solution:**
- Verify `DATABASE_URL` is set correctly
- Check MySQL plugin is running
- Ensure database credentials are valid

```bash
# Test database connection
railway run printenv DATABASE_URL
```

#### 2. Port Binding Errors

**Symptom:** `Address already in use` error

**Solution:**
- Railway automatically assigns the port via `$PORT`
- Ensure `application.properties` uses `${PORT:8080}`
- Don't hardcode port numbers

#### 3. Build Failures

**Symptom:** Maven build fails during deployment

**Solution:**
- Check `pom.xml` for errors
- Verify Java 17 compatibility
- Review build logs for specific errors

```bash
# Test build locally
mvn clean package -DskipTests
```

#### 4. Out of Memory Errors

**Symptom:** Application crashes with OOM errors

**Solution:**
- Increase Railway service memory limit (Project Settings)
- Adjust JVM settings in `railway.json` or `nixpacks.toml`
- Optimize connection pool size in `DatabaseConfig.java`

### Health Check Endpoint

The application exposes a health check at `/` (root path).

**Test locally:**
```bash
curl http://localhost:8080/
```

**Test on Railway:**
```bash
curl https://your-app-name.railway.app/
```

---

## Performance Optimization

### JVM Tuning

The application uses optimized JVM settings for Railway:

```bash
-Xmx768m -Xms256m
-XX:+UseContainerSupport
-XX:MaxRAMPercentage=80.0
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100
-Djava.security.egd=file:/dev/./urandom
```

### Connection Pool Optimization

HikariCP is configured with Railway-optimized settings:

```java
config.setMaximumPoolSize(10);
config.setMinimumIdle(2);
config.setConnectionTimeout(30000); // 30 seconds
config.setIdleTimeout(600000); // 10 minutes
config.setMaxLifetime(1800000); // 30 minutes
```

### Response Compression

Enabled in `application.properties`:

```properties
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,application/javascript,application/json
```

### HTTP/2 Support

```properties
server.http2.enabled=true
```

---

## Architecture

### Technology Stack

- **Framework:** Spring Boot 3.2.0
- **Java Version:** 17 (LTS)
- **Build Tool:** Maven 3.9
- **Database:** MySQL 8.0+
- **Connection Pool:** HikariCP
- **View Technology:** JSP with JSTL
- **Security:** Spring Security with BCrypt

### Application Layers

```
┌─────────────────────────────────────┐
│         Controllers (Web)            │
├─────────────────────────────────────┤
│       Services (Business Logic)      │
├─────────────────────────────────────┤
│      DAOs (Data Access Layer)        │
├─────────────────────────────────────┤
│      HikariCP (Connection Pool)      │
├─────────────────────────────────────┤
│         MySQL Database               │
└─────────────────────────────────────┘
```

### Request Flow

1. User request → Spring Security authentication
2. Controller receives request
3. Service layer processes business logic
4. DAO layer queries database via HikariCP
5. Results returned through layers
6. JSP renders response

---

## Security Best Practices

### 1. Secure Database Connections

The application uses SSL for Railway MySQL connections:

```java
String jdbcUrl = String.format(
    "jdbc:mysql://%s:%d/%s?useSSL=true&requireSSL=true&serverTimezone=UTC",
    host, port, database
);
```

### 2. Password Hashing

All passwords are hashed using BCrypt with strength 10:

```properties
spring.security.password.bcrypt.rounds=10
```

### 3. Environment Variables

Never commit sensitive data to Git. Use Railway environment variables:

```bash
railway variables set ADMIN_PASSWORD="your-secure-password"
```

### 4. Role-Based Access Control

The application enforces role-based access:

- `/admin/**` → Requires `ROLE_ADMIN`
- Protected resources → Require `ROLE_USER` or `ROLE_ADMIN`
- Public routes → `/`, `/login`, `/register`, static resources

---

## Scaling & Production Considerations

### Horizontal Scaling

Railway supports horizontal scaling for increased traffic:

```bash
railway scale --replicas 3
```

**Note:** Ensure session management is stateless or uses shared storage.

### Database Connection Pool

Current settings support ~10 concurrent database connections per instance. Adjust for your needs:

```java
config.setMaximumPoolSize(20); // Increase for high traffic
config.setMinimumIdle(5); // Maintain more idle connections
```

### Monitoring

Use Railway's built-in monitoring:

- CPU usage
- Memory usage
- Network traffic
- Request logs

### Backups

Railway MySQL plugin includes automatic backups. Configure retention:

1. Navigate to MySQL service
2. Click "Settings" → "Backups"
3. Configure backup schedule and retention

---

## Local Development

### Prerequisites

- Java 17
- Maven 3.9+
- MySQL 8.0+ (or use Railway development database)

### Setup

```bash
# Clone repository
git clone https://github.com/martinisapp/martinis.git
cd martinis

# Configure local database
export DATABASE_URL="jdbc:mysql://localhost:3306/martinis?useSSL=false&serverTimezone=UTC"
export ADMIN_USERNAME="admin"
export ADMIN_PASSWORD="password"

# Build and run
mvn clean package
java -jar target/martinis.jar
```

### Using Railway Development Database

```bash
# Link to Railway project
railway link

# Run with Railway environment
railway run java -jar target/martinis.jar
```

---

## Support & Resources

### Documentation

- [Railway Documentation](https://docs.railway.app/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/3.2.0/reference/html/)
- [HikariCP Documentation](https://github.com/brettwooldridge/HikariCP)

### GitHub Repository

- Repository: [martinisapp/martinis](https://github.com/martinisapp/martinis)
- Issues: [Report Issues](https://github.com/martinisapp/martinis/issues)

### Railway Support

- [Railway Discord](https://discord.gg/railway)
- [Railway Help Center](https://help.railway.app/)

---

## Changelog

### Version 2.0 - Complete Railway Rewrite (October 2025)

- Complete rewrite of all Railway configuration files
- Upgraded to Spring Boot 3.2.0
- Optimized JVM settings for Railway containers
- Enhanced database connection handling with SSL support
- Improved logging and monitoring capabilities
- Added PostgreSQL support alongside MySQL
- Comprehensive documentation rewrite
- Performance optimizations for production workloads

---

## License

This application is provided as-is for deployment on Railway.com. See the main repository for licensing details.

---

**Deploy with confidence!** This complete rewrite provides production-ready configuration for Railway deployment.
