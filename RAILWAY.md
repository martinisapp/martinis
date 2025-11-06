# Railway.com Deployment Guide

Complete guide for deploying the Martinis screenplay management application on Railway.com.

## Table of Contents

- [Quick Start](#quick-start)
- [Prerequisites](#prerequisites)
- [Step-by-Step Deployment](#step-by-step-deployment)
- [Environment Variables](#environment-variables)
- [Database Setup](#database-setup)
- [Configuration Files](#configuration-files)
- [Monitoring & Troubleshooting](#monitoring--troubleshooting)
- [Architecture](#architecture)
- [Performance Optimization](#performance-optimization)
- [Security Best Practices](#security-best-practices)

---

## Quick Start

### Deploy from GitHub (Recommended)

1. **Sign in to Railway**
   - Go to [railway.app](https://railway.app)
   - Sign in with your GitHub account

2. **Create New Project**
   - Click "New Project" → "Deploy from GitHub repo"
   - Select the `martinisapp/martinis` repository
   - Select branch to deploy

3. **Add MySQL Database**
   - In your project, click "New" → "Database" → "Add MySQL"
   - Railway automatically sets `DATABASE_URL` environment variable

4. **Configure Environment Variables**
   - Go to your service → "Variables" tab
   - Add required variables (see [Environment Variables](#environment-variables))

5. **Deploy**
   - Railway automatically builds and deploys
   - Monitor progress in "Deployments" tab
   - Access your app at the provided URL

---

## Prerequisites

- Railway.com account (free tier available)
- GitHub account
- Git repository with the Martinis application
- Optional: Railway CLI for advanced usage

### Install Railway CLI (Optional)

```bash
# macOS/Linux
curl -fsSL https://railway.app/install.sh | sh

# Windows (PowerShell)
iwr https://railway.app/install.ps1 | iex

# Verify installation
railway --version

# Login
railway login
```

---

## Step-by-Step Deployment

### Step 1: Create Railway Project

**Using Railway Dashboard:**
1. Visit [railway.app/new](https://railway.app/new)
2. Click "Deploy from GitHub repo"
3. Authorize Railway to access your GitHub repositories
4. Select `martinisapp/martinis`

**Using Railway CLI:**
```bash
# Clone repository
git clone https://github.com/martinisapp/martinis.git
cd martinis

# Initialize Railway project
railway init

# Link to existing project (if already created)
railway link
```

### Step 2: Add MySQL Database

**Using Railway Dashboard:**
1. In your project, click "New"
2. Select "Database"
3. Choose "Add MySQL"
4. Wait for database provisioning (~30 seconds)

**Using Railway CLI:**
```bash
railway add --database mysql
```

Railway automatically creates these environment variables:
- `DATABASE_URL`: MySQL connection string
- `MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`

### Step 3: Configure Environment Variables

**Using Railway Dashboard:**
1. Select your service
2. Go to "Variables" tab
3. Click "New Variable"
4. Add each variable below

**Using Railway CLI:**
```bash
railway variables set ADMIN_USERNAME=admin
railway variables set ADMIN_PASSWORD=your_secure_password_here
railway variables set ADMIN_FIRSTNAME=John
railway variables set ADMIN_LASTNAME=Doe
```

### Step 4: Deploy Application

**Automatic Deployment (Recommended):**
- Push to your GitHub repository
- Railway automatically detects changes and deploys

```bash
git add .
git commit -m "Configure for Railway"
git push origin main
```

**Manual Deployment with CLI:**
```bash
railway up
```

### Step 5: Monitor Deployment

**Using Railway Dashboard:**
1. Go to "Deployments" tab
2. Click on the latest deployment
3. View real-time build and deployment logs

**Using Railway CLI:**
```bash
# View logs
railway logs

# Follow logs in real-time
railway logs -f
```

### Step 6: Access Your Application

Once deployed, Railway provides a public URL:

**Using Railway Dashboard:**
- Go to "Settings" → "Domains"
- Click "Generate Domain" if not already created
- Access your app at: `https://your-app-name.railway.app`

**Using Railway CLI:**
```bash
railway open
```

---

## Environment Variables

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `ADMIN_USERNAME` | Initial admin username | `admin` |
| `ADMIN_PASSWORD` | Initial admin password | `SecurePass123!` |
| `ADMIN_FIRSTNAME` | Admin first name | `John` |
| `ADMIN_LASTNAME` | Admin last name | `Doe` |

### Automatic Variables (Set by Railway)

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | MySQL connection URL | `mysql://user:pass@host:3306/db` |
| `PORT` | Application port | `8080` |
| `RAILWAY_ENVIRONMENT` | Deployment environment | `production` |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SQL_INIT_MODE` | Schema initialization mode | `always` |
| `LOGGING_LEVEL_ROOT` | Root logging level | `INFO` |
| `SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE` | Max DB connections | `10` |
| `SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE` | Min idle connections | `2` |

### Setting Environment Variables

**Railway Dashboard Method:**
1. Select your service
2. Go to "Variables" tab
3. Click "New Variable"
4. Enter key and value
5. Click "Add"

**Railway CLI Method:**
```bash
# Set single variable
railway variables set KEY=value

# Set multiple variables
railway variables set \
  ADMIN_USERNAME=admin \
  ADMIN_PASSWORD=secure123 \
  ADMIN_FIRSTNAME=John
```

**Template File:**
See `.env.railway` for a complete template of all available variables.

---

## Database Setup

### Automatic Schema Initialization

The application automatically creates all required database tables on first startup:

**Tables Created:**
- `user` - User accounts with encrypted passwords
- `authority` - User roles (ROLE_USER, ROLE_ADMIN)
- `project` - Screenplay projects
- `scene` - Scenes within projects
- `actor` - Actor database
- `person` - Characters in projects
- `block` - Screenplay blocks/lines with bookmarking

**How It Works:**
1. Application starts and connects to MySQL
2. Checks if `user` table exists
3. If not, runs `schema.sql` from `src/main/resources/`
4. Creates initial admin user from environment variables

### Database Connection

Railway automatically provides `DATABASE_URL` in format:
```
mysql://user:password@host:3306/database
```

The application's `DatabaseConfig.java` automatically:
- Parses Railway's MySQL URL format
- Converts to JDBC format with SSL
- Configures HikariCP connection pool
- Enables performance optimizations

### Manual Database Access

**Using Railway Dashboard:**
1. Select MySQL database service
2. Go to "Connect" tab
3. Copy connection command
4. Run in terminal

**Using Railway CLI:**
```bash
railway connect mysql
```

**Using MySQL Client:**
```bash
# Get connection details
railway variables

# Connect using mysql client
mysql -h host -u user -p database
```

### Database Migrations

**First Deployment:**
```bash
railway variables set SQL_INIT_MODE=always
```

**Subsequent Deployments (skip schema creation):**
```bash
railway variables set SQL_INIT_MODE=never
```

---

## Configuration Files

### 1. `railway.json` - Railway Service Configuration

Defines how Railway builds and deploys your application:

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "Dockerfile",
    "watchPatterns": ["src/**", "pom.xml", "Dockerfile"]
  },
  "deploy": {
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10,
    "healthcheckPath": "/actuator/health",
    "healthcheckTimeout": 10000
  }
}
```

**Key Settings:**
- `builder: DOCKERFILE` - Uses Docker for builds
- `watchPatterns` - Files that trigger rebuilds
- `healthcheckPath` - Endpoint for health checks
- `restartPolicyType` - Restart on failure

### 2. `Dockerfile` - Container Build Configuration

Multi-stage Docker build optimized for Railway:

**Build Stage:**
- Uses Maven 3.9 with Java 17
- Caches dependencies for faster rebuilds
- Compiles application to JAR

**Runtime Stage:**
- Uses minimal JRE Alpine image
- Runs as non-root user for security
- Optimized JVM settings for containers

### 3. `nixpacks.toml` - Alternative Build System

Alternative to Docker using Railway's Nixpacks:

```toml
[phases.setup]
nixPkgs = ["maven_3_9", "jdk17_headless"]

[phases.build]
cmds = ["mvn clean package -DskipTests -B"]

[start]
cmd = "java -jar target/martinis.jar"
```

**To use Nixpacks instead of Docker:**
1. Update `railway.json`: `"builder": "NIXPACKS"`
2. Railway will automatically use `nixpacks.toml`

### 4. `.railwayignore` - Deployment Exclusions

Excludes unnecessary files from deployment:
- Documentation files
- IDE configuration
- Test files
- Local environment files
- Build artifacts (rebuilt on Railway)

### 5. `application.properties` - Spring Boot Configuration

Main application configuration:
- Server port from `$PORT` environment variable
- Database URL from `$DATABASE_URL`
- HikariCP connection pool settings
- JSP view configuration
- Security settings

---

## Monitoring & Troubleshooting

### View Application Logs

**Railway Dashboard:**
1. Select your service
2. Go to "Deployments" tab
3. Click on deployment
4. View logs in real-time

**Railway CLI:**
```bash
# View recent logs
railway logs

# Follow logs in real-time
railway logs -f

# Filter logs
railway logs | grep ERROR
```

### Health Check Endpoint

Railway uses `/actuator/health` to monitor application health:

```bash
# Check health locally
curl http://localhost:8080/actuator/health

# Check health on Railway
curl https://your-app.railway.app/actuator/health
```

**Expected Response:**
```json
{"status":"UP"}
```

### Common Issues

#### 1. Database Connection Failed

**Symptoms:**
- Application fails to start
- Logs show database connection errors

**Solutions:**
```bash
# Verify DATABASE_URL is set
railway variables

# Check MySQL service is running
railway status

# Test database connection
railway connect mysql
```

#### 2. Port Binding Issues

**Symptoms:**
- "Address already in use" error
- Application won't start

**Solutions:**
- Ensure `application.properties` uses `${PORT:8080}`
- Don't hardcode port in code
- Railway automatically assigns port via `$PORT`

#### 3. Build Failures

**Symptoms:**
- Build fails during Maven compilation
- "BUILD FAILURE" in logs

**Solutions:**
```bash
# Test build locally
mvn clean package

# Check Java version
java -version

# Verify pom.xml is valid
mvn validate
```

#### 4. Out of Memory Errors

**Symptoms:**
- Application crashes with OOM
- `OutOfMemoryError` in logs

**Solutions:**
1. Increase Railway plan memory limit
2. Optimize JVM settings in Dockerfile
3. Reduce connection pool size
4. Review application memory usage

```bash
# Update connection pool
railway variables set SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=5
```

#### 5. Schema Initialization Issues

**Symptoms:**
- Tables not created
- "Table doesn't exist" errors

**Solutions:**
```bash
# Ensure SQL_INIT_MODE is set
railway variables set SQL_INIT_MODE=always

# Check schema.sql exists
ls src/main/resources/schema.sql

# View logs for initialization errors
railway logs | grep "SQL"
```

### Performance Monitoring

**Railway Dashboard Metrics:**
- CPU usage
- Memory usage
- Network traffic
- Request count
- Response times

**Access Metrics:**
1. Select your service
2. Go to "Metrics" tab
3. View real-time and historical data

---

## Architecture

### Technology Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.3.3 |
| Language | Java 17 (LTS) |
| Build Tool | Maven 3.9+ |
| Database | MySQL 8.0+ |
| Connection Pool | HikariCP |
| View Technology | JSP + JSTL |
| Security | Spring Security + BCrypt |
| Container | Docker (Alpine Linux) |

### Application Layers

```
┌─────────────────────────────────┐
│      Controllers (Web)          │ ← HTTP Requests
├─────────────────────────────────┤
│      Services (Business)         │ ← Business Logic
├─────────────────────────────────┤
│      DAOs (Data Access)          │ ← Database Operations
├─────────────────────────────────┤
│   HikariCP (Connection Pool)     │ ← Connection Management
├─────────────────────────────────┤
│      MySQL Database              │ ← Data Storage
└─────────────────────────────────┘
```

### Request Flow

1. User request → Railway load balancer
2. Spring Security authentication
3. Controller processes request
4. Service executes business logic
5. DAO queries database via HikariCP
6. Results returned through layers
7. JSP renders HTML response
8. Response sent to user

### Key Components

**Controllers:**
- `HomeController` - Dashboard and home page
- `LoginController` - Authentication
- `ProjectController` - Project management
- `SceneController` - Scene management
- `ActorController` - Actor database
- `PersonController` - Character management
- `BlockController` - Screenplay blocks

**Services:**
- `UserService` - User management
- `ProjectService` - Project operations
- `SceneService` - Scene operations
- `ActorService` - Actor operations

**Configuration:**
- `DatabaseConfig` - Database connection
- `SecurityConfig` - Spring Security
- `WebConfig` - MVC configuration
- `DatabaseInitializer` - Schema initialization

---

## Performance Optimization

### JVM Tuning

The Dockerfile includes optimized JVM settings for Railway:

```bash
-XX:+UseContainerSupport       # Respect container limits
-XX:MaxRAMPercentage=75.0      # Use up to 75% of memory
-XX:+UseG1GC                   # Low-latency GC
-XX:MaxGCPauseMillis=100       # Max GC pause time
-XX:+UseStringDeduplication    # Reduce memory usage
```

### Connection Pool Optimization

HikariCP configured for Railway:

```java
Maximum Pool Size: 10 connections
Minimum Idle: 2 connections
Connection Timeout: 30 seconds
Idle Timeout: 10 minutes
Max Lifetime: 30 minutes
```

**For high traffic, increase pool size:**
```bash
railway variables set SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
railway variables set SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
```

### HTTP Compression

Enabled in `application.properties`:
- Compresses HTML, CSS, JavaScript, JSON
- Minimum response size: 1KB
- Reduces bandwidth by ~70%

### HTTP/2 Support

Enabled for faster page loads:
- Multiplexing multiple requests
- Server push capability
- Header compression

### Build Optimization

**Layer Caching:**
- POM file copied first
- Dependencies cached between builds
- Only source changes trigger full rebuild

**Image Size:**
- Multi-stage build reduces final image
- Alpine Linux base (~50MB vs ~200MB)
- Only JRE included (no JDK)

---

## Security Best Practices

### 1. SSL/TLS Encryption

**Railway provides:**
- Automatic HTTPS for all domains
- TLS 1.3 support
- Free SSL certificates

**Application enforces:**
- SSL for database connections
- Secure cookie settings
- HTTPS redirect (configurable)

### 2. Password Security

**BCrypt hashing:**
- Strength: 10 rounds
- Salt automatically generated
- Passwords never stored in plain text

```java
// Configured in SecurityConfig.java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
}
```

### 3. Environment Variables

**Never commit sensitive data:**
- Use Railway environment variables
- Keep `.env` files out of Git
- Use strong admin passwords

```bash
# Good password examples
railway variables set ADMIN_PASSWORD="MySecure!Pass123"

# Bad password examples (DON'T USE)
ADMIN_PASSWORD=password
ADMIN_PASSWORD=admin123
```

### 4. Role-Based Access Control

**Access levels:**
- `/admin/**` → Requires `ROLE_ADMIN`
- Protected routes → Require `ROLE_USER`
- Public routes → `/`, `/login`, `/register`

### 5. Database Security

**Connection security:**
- SSL/TLS encryption required
- Credentials from environment only
- Connection pool prevents exhaustion
- Prepared statements prevent SQL injection

### 6. Security Headers

Spring Security automatically adds:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security` (HSTS)

---

## Scaling & Production

### Horizontal Scaling

Railway supports multiple replicas:

**Using Railway Dashboard:**
1. Select your service
2. Go to "Settings"
3. Update "Replicas" count

**Using Railway CLI:**
```bash
# Scale to 3 replicas
railway service scale --replicas 3
```

**Note:** Ensure your application is stateless or uses shared session storage.

### Vertical Scaling

Upgrade Railway plan for more resources:
- Hobby: 512MB RAM, 1 vCPU
- Pro: Up to 8GB RAM, 8 vCPU
- Custom enterprise plans available

### Database Backups

Railway MySQL includes automatic backups:

**Configure backups:**
1. Select MySQL service
2. Go to "Settings" → "Backups"
3. Set backup frequency
4. Set retention period

**Restore backup:**
1. Select backup from list
2. Click "Restore"
3. Confirm restoration

### Monitoring & Alerts

**Set up alerts in Railway:**
1. Go to project settings
2. Configure notification webhooks
3. Set alert thresholds:
   - High CPU usage
   - High memory usage
   - Failed deployments
   - Health check failures

---

## Local Development

### Prerequisites

- Java 17 or higher
- Maven 3.9 or higher
- MySQL 8.0+ (or use Railway dev database)

### Setup with Local MySQL

```bash
# Clone repository
git clone https://github.com/martinisapp/martinis.git
cd martinis

# Configure environment
export DATABASE_URL="jdbc:mysql://localhost:3306/martinis?useSSL=false&serverTimezone=UTC"
export ADMIN_USERNAME="admin"
export ADMIN_PASSWORD="password"

# Build and run
mvn clean package
java -jar target/martinis.jar

# Access application
open http://localhost:8080
```

### Setup with Railway Database

```bash
# Link to Railway project
railway link

# Run with Railway environment
railway run mvn spring-boot:run

# Or run JAR with Railway environment
mvn clean package
railway run java -jar target/martinis.jar
```

### Development with Docker Compose

```bash
# Start MySQL and application
docker-compose up

# Stop services
docker-compose down

# View logs
docker-compose logs -f
```

---

## Support & Resources

### Documentation

- **Railway Docs:** [docs.railway.app](https://docs.railway.app)
- **Spring Boot Docs:** [spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
- **HikariCP Docs:** [github.com/brettwooldridge/HikariCP](https://github.com/brettwooldridge/HikariCP)

### Community Support

- **Railway Discord:** [discord.gg/railway](https://discord.gg/railway)
- **Railway Help Center:** [help.railway.app](https://help.railway.app)
- **GitHub Issues:** [github.com/martinisapp/martinis/issues](https://github.com/martinisapp/martinis/issues)

### Getting Help

1. Check this guide first
2. Review Railway documentation
3. Check application logs
4. Search GitHub issues
5. Ask in Railway Discord
6. Create GitHub issue

---

## Changelog

### 2025-01-06 - Complete Railway Rewrite

- ✅ Rewritten Dockerfile with Alpine Linux for smaller image
- ✅ Created nixpacks.toml for alternative build method
- ✅ Updated railway.json with optimized settings
- ✅ Cleaned up .railwayignore for faster deployments
- ✅ Simplified .env.railway template
- ✅ Comprehensive RAILWAY.md documentation
- ✅ Fixed Spring Boot version in configuration
- ✅ Optimized JVM settings for Railway containers
- ✅ Enhanced security with non-root user
- ✅ Improved health check configuration
- ✅ Updated connection pool settings
- ✅ Better layer caching for faster builds

---

## License

This application is provided as-is for deployment on Railway.com. See the main repository for licensing details.

---

**🚀 Ready to deploy!** This complete rewrite provides production-ready configuration for Railway.com deployment.
