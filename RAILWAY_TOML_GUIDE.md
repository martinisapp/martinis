# Railway Template Configuration Guide

This document explains the `railway.toml` file that enables automatic MySQL provisioning for the Martinis application.

## Overview

The `railway.toml` file is Railway's template configuration format that defines multiple services to be deployed together. When someone clicks the "Deploy to Railway" button, this file tells Railway to automatically create both the application and a MySQL database.

## File Structure

The configuration defines two services:

### 1. Application Service (martinis)

```toml
[[services]]
name = "martinis"
source = { repo = "martinisapp/martinis" }
```

This service:
- Builds and deploys the Spring Boot application
- Uses Nixpacks for building (configured via `nixpacks.toml`)
- Includes health checks and restart policies
- Automatically sets admin user environment variables

### 2. MySQL Service (mysql)

```toml
[[services]]
name = "mysql"
source = { image = "mysql:8.0" }
```

This service:
- Deploys MySQL 8.0 from the official Docker image
- Auto-generates secure credentials for root and application user
- Creates the `martinis` database automatically
- Includes health checks via `mysqladmin ping`

## Service Connection

The services are connected via service references in environment variables:

```toml
[[services.env]]
name = "DATABASE_URL"
value = "mysql://${mysql.MYSQL_USER}:${mysql.MYSQL_PASSWORD}@${mysql.RAILWAY_PRIVATE_DOMAIN}:3306/${mysql.MYSQL_DATABASE}?..."
```

Railway automatically resolves these references:
- `${mysql.MYSQL_USER}` → MySQL user from the database service
- `${mysql.MYSQL_PASSWORD}` → MySQL password from the database service
- `${mysql.RAILWAY_PRIVATE_DOMAIN}` → Internal DNS name for the MySQL service
- `${mysql.MYSQL_DATABASE}` → Database name

## How It Works

1. **User clicks "Deploy to Railway" button**
2. **Railway reads `railway.toml`**
3. **Railway creates two services:**
   - MySQL database with auto-generated credentials
   - Martinis application connected to the database
4. **Railway interpolates service references:**
   - `DATABASE_URL` is automatically constructed with actual values
5. **Application starts and connects to MySQL:**
   - `DatabaseConfig.java` reads `DATABASE_URL`
   - Schema is initialized automatically
   - Application is ready to use

## Benefits

- **Zero Configuration**: Users don't need to manually create MySQL
- **Secure by Default**: Passwords are auto-generated
- **Private Networking**: Services communicate via Railway's private network
- **Automatic Connection**: No manual DATABASE_URL configuration needed
- **Reproducible**: Same setup every time

## Template Deployment URL

When you fork this repository, users can deploy with:

```
https://railway.com/template?referralCode=YOUR_CODE
```

Or directly from GitHub:

```
https://railway.com/new/github?repo=martinisapp/martinis
```

## Customization

### Adding Environment Variables

Add to the `martinis` service:

```toml
[[services.env]]
name = "YOUR_VAR"
value = "your-value"
```

Or use `generateValue = true` for auto-generated secrets:

```toml
[[services.env]]
name = "SECRET_KEY"
generateValue = true
```

### Changing MySQL Version

Modify the MySQL service:

```toml
source = { image = "mysql:8.4" }  # Use specific version
```

### Adding More Services

Add a new service block:

```toml
[[services]]
name = "redis"
source = { image = "redis:7-alpine" }
```

## Testing the Template

To test the template configuration:

1. **Fork the repository** (if you haven't already)
2. **Push your changes** to the main branch
3. **Visit Railway template URL:**
   ```
   https://railway.com/new/github?repo=YOUR_USERNAME/martinis
   ```
4. **Click Deploy** and verify both services are created

## Troubleshooting

### MySQL Not Created
- Verify `railway.toml` is in the root directory
- Check TOML syntax is valid
- Ensure both `[[services]]` blocks are present

### Connection Errors
- Verify `DATABASE_URL` uses service references correctly
- Check MySQL service is healthy in Railway dashboard
- Review logs for connection errors

### Template Not Loading
- Ensure `railway.toml` is committed to the repository
- Verify the repository is public or accessible to Railway
- Check Railway's status page for service issues

## Learn More

- [Railway Templates Documentation](https://docs.railway.app/deploy/railway-toml)
- [Railway Service References](https://docs.railway.app/deploy/variables#service-variables)
- [MySQL on Railway](https://docs.railway.app/databases/mysql)

## Related Files

- `railway.toml` - This configuration file
- `railway.json` - Legacy single-service configuration (still supported)
- `nixpacks.toml` - Build configuration for the application
- `app.json` - Additional metadata for deployments
