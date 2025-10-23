# Automatic MySQL Setup for Railway.com

This guide explains how to automatically set up MySQL when deploying the Martinis application to Railway.com.

## Fastest Method: Deploy to Railway Button

The easiest way to deploy Martinis with MySQL is using the "Deploy to Railway" button:

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/template?code=https://github.com/martinisapp/martinis)

When you click this button, Railway **automatically**:
1. Creates a new project
2. Provisions a MySQL 8.0 database service
3. Generates secure database credentials
4. Deploys the application
5. Connects the app to the database

**No manual MySQL setup required!** The `railway.toml` file in the repository defines both services (app and MySQL), so everything is configured automatically.

## Manual Setup (Alternative Method)

If you prefer to set up MySQL manually or are working with an existing Railway project:

### 1. Add MySQL Database to Your Railway Project

Railway will automatically provide the `DATABASE_URL` environment variable when you add a MySQL service:

```bash
# Option A: Using Railway CLI
railway add --mysql

# Option B: Using Railway Dashboard
# 1. Open your project in Railway dashboard
# 2. Click "+ New" button
# 3. Select "Database" → "Add MySQL"
```

### 2. Deploy Your Application

Once MySQL is added, Railway will automatically:
- Provide the `DATABASE_URL` environment variable in Railway's format
- Connect your application to the MySQL database
- Run schema initialization on first startup
- Create database tables automatically

That's it! Your application will automatically configure itself.

## How It Works

### Automatic Database Connection

The application uses `DatabaseConfig.java` which automatically:

1. **Detects Railway's MySQL URL format**: `mysql://user:password@host:port/database`
2. **Converts to JDBC format**: `jdbc:mysql://host:port/database?useSSL=true&...`
3. **Extracts credentials** from the URL
4. **Configures HikariCP connection pool** with Railway-optimized settings

See: `src/main/java/com/chriswatnee/martinis/config/DatabaseConfig.java:122-147`

### Automatic Schema Initialization

On first startup, the application automatically:

1. **Runs `schema.sql`** to create all required tables
2. **Uses `CREATE TABLE IF NOT EXISTS`** to avoid errors on redeployment
3. **Preserves existing data** across deployments

See: `src/main/resources/schema.sql`

### Automatic Admin User Creation

Set these environment variables in Railway to create an initial admin user:

```bash
ADMIN_USERNAME=your_admin_username
ADMIN_PASSWORD=your_secure_password
ADMIN_FIRSTNAME=Admin
ADMIN_LASTNAME=User
```

The application will automatically create the admin user on first startup.

## Railway Environment Variables

### Required Variables (Auto-configured by Railway)

- `DATABASE_URL` - Automatically provided by Railway MySQL service
- `PORT` - Automatically provided by Railway

### Optional Variables (Recommended to Set)

```bash
# Admin user creation (highly recommended)
ADMIN_USERNAME=admin
ADMIN_PASSWORD=<strong-password-here>
ADMIN_FIRSTNAME=Admin
ADMIN_LASTNAME=User

# SQL initialization mode (default: always)
SQL_INIT_MODE=always  # Options: always, never, embedded
```

## Setting Environment Variables in Railway

### Using Railway CLI:

```bash
railway variables set ADMIN_USERNAME=admin
railway variables set ADMIN_PASSWORD=your_secure_password
railway variables set ADMIN_FIRSTNAME=Admin
railway variables set ADMIN_LASTNAME=User
```

### Using Railway Dashboard:

1. Open your project in Railway dashboard
2. Select your service
3. Go to "Variables" tab
4. Click "+ New Variable"
5. Add each variable and its value

## Deployment Checklist

- [ ] MySQL database added to Railway project
- [ ] `ADMIN_USERNAME` environment variable set
- [ ] `ADMIN_PASSWORD` environment variable set (use a strong password!)
- [ ] `ADMIN_FIRSTNAME` environment variable set
- [ ] `ADMIN_LASTNAME` environment variable set
- [ ] Application deployed and started successfully
- [ ] Check logs to verify database connection
- [ ] Access application URL to verify it's running

## Verifying Deployment

### 1. Check Deployment Logs

Look for these success messages in Railway logs:

```
✓ Initializing HikariCP DataSource for Railway deployment
✓ Detected Railway MySQL URL format
✓ Database credentials extracted from URL
✓ MySQL JDBC URL configured: jdbc:mysql://...
✓ HikariCP DataSource configured successfully
```

### 2. Check Database Connection

The application includes a healthcheck endpoint:

```bash
curl https://your-app.railway.app/
```

If you see the application homepage, the database connection is working!

## Troubleshooting

### Issue: "Failed to configure database connection"

**Solution**: Ensure MySQL service is added and linked to your application
```bash
railway add --mysql
```

### Issue: "Access denied for user"

**Solution**: Railway automatically configures credentials. This usually resolves after a redeploy:
```bash
railway up --detach
```

### Issue: Tables not created

**Solution**: Check the `SQL_INIT_MODE` variable:
```bash
railway variables set SQL_INIT_MODE=always
```

### Issue: Cannot login with admin credentials

**Solution**: Verify environment variables are set correctly:
```bash
railway variables
```

Look for `ADMIN_USERNAME`, `ADMIN_PASSWORD`, `ADMIN_FIRSTNAME`, `ADMIN_LASTNAME`

## Database Schema Management

### Schema Changes

The application uses Spring's SQL initialization with `schema.sql`. To modify the schema:

1. Update `src/main/resources/schema.sql`
2. Use `CREATE TABLE IF NOT EXISTS` for new tables
3. Use conditional `ALTER TABLE` for new columns (see existing examples)
4. Deploy the changes

### Manual Database Access

To access your Railway MySQL database directly:

```bash
# Using Railway CLI
railway connect MySQL

# Or get connection details
railway variables
# Look for DATABASE_URL
```

## Railway-Specific Optimizations

The application includes several Railway-specific optimizations:

### Connection Pool Settings

- **Max Pool Size**: 10 (suitable for Railway's resource limits)
- **Min Idle**: 2 (maintains ready connections)
- **Connection Timeout**: 30 seconds
- **Idle Timeout**: 10 minutes
- **Max Lifetime**: 30 minutes

See: `src/main/java/com/chriswatnee/martinis/config/DatabaseConfig.java:66-71`

### Performance Optimizations

- **Prepared Statement Caching**: Enabled
- **Batch Statement Rewriting**: Enabled
- **Server-side Prepared Statements**: Enabled
- **Result Set Metadata Caching**: Enabled

See: `src/main/java/com/chriswatnee/martinis/config/DatabaseConfig.java:74-83`

### SSL/TLS Configuration

Railway MySQL connections use SSL by default:
```
jdbc:mysql://host:port/db?useSSL=true&requireSSL=true&serverTimezone=UTC
```

## Cost Considerations

Railway MySQL pricing:

- **Free Tier**: Includes $5 credit per month (sufficient for development)
- **Pro Plan**: $0.000463 per GB-hour for database storage
- **Outbound Data**: First 100 GB free per month

For production workloads, monitor your usage in the Railway dashboard.

## Migration from Other Databases

If you're migrating from PostgreSQL or another database:

1. Export your data from the old database
2. Add MySQL service to Railway
3. Import data using Railway's MySQL CLI:
   ```bash
   railway connect MySQL < backup.sql
   ```

The `DatabaseConfig` supports both MySQL and PostgreSQL URL formats automatically.

## Support

For Railway-specific issues:
- Railway Documentation: https://docs.railway.app/
- Railway Discord: https://discord.gg/railway
- Railway Support: https://railway.app/help

For Martinis application issues:
- Check application logs in Railway dashboard
- Review `src/main/java/com/chriswatnee/martinis/config/DatabaseConfig.java`
