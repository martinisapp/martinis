# Railway Deployment Guide - Modernized Spring Boot

This application has been rewritten to use modern Spring Boot 3.x with Java 17 for streamlined Railway deployment.

## What Changed

### Modernization
- **Spring Boot 3.2.0** (from Spring MVC 4.3.30)
- **Java 17** (from Java 8)
- **Embedded Tomcat** (no more webapp-runner)
- **Simplified configuration** - Java-based config instead of XML
- **HikariCP connection pooling** (modern, faster)
- **Single JAR deployment** (instead of WAR)

### Configuration
All XML configurations have been migrated to:
- `application.properties` - Application settings
- `SecurityConfig.java` - Security configuration
- `DatabaseConfig.java` - Database and connection pooling
- `WebConfig.java` - Web and multipart file upload settings

## Quick Deploy to Railway

### 1. Create Railway Project

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Create new project
railway init
```

### 2. Add MySQL Database

In Railway dashboard:
1. Click "+ New"
2. Select "Database" → "MySQL"
3. Railway automatically provides `DATABASE_URL` environment variable

### 3. Deploy Application

```bash
# Link to your project
railway link

# Deploy
railway up
```

Or connect your GitHub repo in Railway dashboard for automatic deployments.

### 4. Generate Public Domain

In Railway dashboard:
1. Click on your service
2. Go to "Settings" → "Networking"
3. Click "Generate Domain"

## Environment Variables

Railway automatically provides:
- `PORT` - Application port (Spring Boot uses this)
- `DATABASE_URL` - MySQL connection URL in format: `mysql://user:pass@host:port/db`

The application automatically parses Railway's `DATABASE_URL` format.

## Build Configuration

The application uses:
- **nixpacks.toml** - Defines Java 17 and Maven build
- **Procfile** - Simple JAR execution command

### Build Process
```bash
mvn clean package -DskipTests
java -jar target/martinis.jar
```

## Local Development

### Prerequisites
- Java 17
- Maven 3.6+
- MySQL 8.0+

### Run Locally

1. Set up local MySQL database:
```bash
mysql -u root -p
CREATE DATABASE martinis;
```

2. Set environment variable:
```bash
export DATABASE_URL=jdbc:mysql://localhost:3306/martinis?useSSL=false&serverTimezone=UTC
```

3. Run application:
```bash
mvn spring-boot:run
```

4. Access at http://localhost:8080

## Database Schema

The application automatically initializes the database schema from `src/main/resources/schema.sql` on first startup.

To skip schema initialization on subsequent deployments:
```bash
railway variables set SKIP_SCHEMA_INIT=true
```

## Troubleshooting

### Build Fails
Check Railway deployment logs. Common issues:
- Maven dependency resolution (usually auto-resolves on retry)
- Java version mismatch (ensure Java 17)

### Database Connection Errors
1. Verify MySQL service is running in Railway
2. Check `DATABASE_URL` is set: `railway variables`
3. View logs: `railway logs`

### Application Won't Start
1. Check logs for errors: `railway logs`
2. Verify `PORT` environment variable is set
3. Ensure database schema is initialized

### JSP Not Rendering
- JSPs must be in `src/main/webapp/WEB-INF/jsp/`
- Spring Boot automatically configures JSP support with tomcat-embed-jasper

## Key Files

- `pom.xml` - Spring Boot dependencies and build config
- `MartinisApplication.java` - Spring Boot main application
- `application.properties` - Application configuration
- `config/SecurityConfig.java` - Security settings
- `config/DatabaseConfig.java` - Database configuration
- `config/WebConfig.java` - Web and upload settings
- `nixpacks.toml` - Railway build configuration
- `Procfile` - Railway start command

## Benefits of This Rewrite

1. **Simpler Deployment** - Single JAR, no external servlet container
2. **Modern Stack** - Latest Spring Boot, Java 17, security patches
3. **Better Performance** - HikariCP, optimized defaults
4. **Easier Configuration** - Properties files instead of XML
5. **Railway Optimized** - Automatic DATABASE_URL parsing
6. **Faster Startup** - Optimized connection pooling and initialization

## Migration Notes

Old XML configurations have been backed up with `.old` extension:
- `web.xml.old`
- `spring-dispatcher-servlet.xml.old`
- `spring-security.xml.old`
- `spring-persistence.xml.old`

These are no longer used but kept for reference.

## Support

For Railway-specific issues:
- [Railway Documentation](https://docs.railway.app/)
- [Railway Discord](https://discord.gg/railway)
