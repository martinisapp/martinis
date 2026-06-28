# 🔐 Railway Environment Variables Reference

Complete guide to all Martinis environment variables for Railway deployment.

## Quick Reference

**Required Variables:**
```bash
ADMIN_USERNAME=admin
ADMIN_PASSWORD=SecurePassword123!
ADMIN_FIRSTNAME=Admin
ADMIN_LASTNAME=User
```

**Automatic Variables (Set by Railway):**
```bash
DATABASE_URL=mysql://user:pass@host:3306/db
PORT=8080
RAILWAY_ENVIRONMENT=production
```

---

## Table of Contents
- [Required Variables](#required-variables)
- [Database Configuration](#database-configuration)
- [Application Configuration](#application-configuration)
- [Performance Tuning](#performance-tuning)
- [Security Settings](#security-settings)
- [Logging Configuration](#logging-configuration)
- [Setting Variables](#setting-variables)
- [Validation Checklist](#validation-checklist)

---

## Required Variables

These must be set before first deployment.

### ADMIN_USERNAME
**Purpose:** Initial admin account username

**Type:** String  
**Required:** Yes (first deployment only)  
**Default:** None  
**Example:** `admin`, `administrator`, `superuser`

**Best Practices:**
- Use memorable username
- At least 3 characters
- Alphanumeric (no special chars for username)

```bash
railway variables set ADMIN_USERNAME=admin
```

**⚠️ Important:**
- Only used to create initial admin account
- Can create additional accounts after login
- Changing this variable after first deploy doesn't change existing account

---

### ADMIN_PASSWORD
**Purpose:** Initial admin account password

**Type:** String  
**Required:** Yes (first deployment only)  
**Default:** None  
**Example:** `MySecure!Pass123`

**Security Requirements:**
- Minimum 8 characters (recommend 16+)
- Mix uppercase and lowercase
- Include numbers
- Include special characters: `! @ # $ % ^ & *`
- NOT easy dictionary words
- NOT the same as username

**Strong Password Examples:**
```
✓ MySecure!Pass123
✓ Martinis@2025Project!
✓ Screen#Writing$Prod42
✓ xK9@mL2#pQ5$vW8!jH3
```

**Weak Password Examples:**
```
✗ password
✗ admin123
✗ martinis
✗ 12345678
✗ qwerty
```

```bash
railway variables set ADMIN_PASSWORD="MySecure!Pass123"
```

**⚠️ Important:**
- Never commit password to Git
- Use Railway environment variables only
- Change password via admin panel after login
- Use password manager to store

---

### ADMIN_FIRSTNAME
**Purpose:** First name for initial admin account

**Type:** String  
**Required:** Yes (first deployment only)  
**Default:** None  
**Example:** `John`, `Alice`, `System`

```bash
railway variables set ADMIN_FIRSTNAME=John
```

---

### ADMIN_LASTNAME
**Purpose:** Last name for initial admin account

**Type:** String  
**Required:** Yes (first deployment only)  
**Default:** None  
**Example:** `Doe`, `Smith`, `Administrator`

```bash
railway variables set ADMIN_LASTNAME=Doe
```

---

## Database Configuration

### DATABASE_URL
**Purpose:** MySQL connection string

**Type:** String  
**Set By:** Railway (automatic)  
**Format:** `mysql://user:password@host:port/database`  
**Example:**
```
mysql://martinis_user:pass123@aws-0-us-east-1.railway.app:3306/railway
```

**⚠️ Important:**
- Automatically provided by Railway
- DO NOT manually set
- Parsed and converted to JDBC format by `DatabaseConfig.java`

**Verify It's Set:**
```bash
railway variables | grep DATABASE_URL
# Should output something like:
# DATABASE_URL = mysql://user:pass@host:3306/db
```

**If Missing:**
```bash
# MySQL service wasn't linked
# Delete and re-add:
railway service delete mysql
railway add --database mysql
railway redeploy
```

---

### SQL_INIT_MODE
**Purpose:** Control database schema initialization

**Type:** String  
**Values:** `always` | `never` | `embedded`  
**Default:** `always`  
**Example:** `always`

**Behavior:**

| Value | Behavior |
|-------|----------|
| `always` | Run `schema.sql` on every startup |
| `never` | Never run schema initialization |
| `embedded` | Run only if embedded database |

**Use Cases:**

```bash
# First deployment (create schema)
railway variables set SQL_INIT_MODE=always
railway redeploy
# Wait for schema creation

# Subsequent deployments (skip schema)
railway variables set SQL_INIT_MODE=never
railway redeploy
```

**When to Use Each:**

1. **`always`** → First deployment, after deleting database
   ```bash
   railway variables set SQL_INIT_MODE=always
   railway redeploy
   ```

2. **`never`** → Subsequent deployments (faster)
   ```bash
   railway variables set SQL_INIT_MODE=never
   railway redeploy
   ```

3. **`embedded`** → Default, only for embedded databases (not needed for Railway)

---

## Application Configuration

### PORT
**Purpose:** Application server port

**Type:** Integer  
**Set By:** Railway (automatic)  
**Default:** `8080`  
**Example:** `8080`

**⚠️ Important:**
- Automatically assigned by Railway
- DO NOT manually set
- Application reads from `$PORT` environment variable
- Must be set in `application.properties`: `server.port=${PORT:8080}`

**Verify:**
```bash
railway variables | grep PORT
# Likely empty (Railway sets automatically)

# Or check logs
railway logs | grep "port"
```

---

### JAVA_OPTS
**Purpose:** JVM startup options

**Type:** String  
**Set By:** Dockerfile (automatic)  
**Default:** See Dockerfile  
**Example:** `-Xmx768m -XX:+UseG1GC`

**Current JVM Options:**
```
-XX:+UseContainerSupport
-XX:MaxRAMPercentage=75.0
-XX:InitialRAMPercentage=50.0
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100
-XX:+ParallelRefProcEnabled
-XX:+UseStringDeduplication
-XX:+ExitOnOutOfMemoryError
```

**⚠️ Important:**
- Configured in `Dockerfile`
- Generally should NOT be overridden
- Only customize if expert knowledge
- Dockerfile already optimized for Railway

---

## Performance Tuning

### SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE
**Purpose:** Maximum database connections

**Type:** Integer  
**Default:** `10`  
**Recommended Range:** `5-20`  
**Example:** `10`

**When to Increase:**
- High concurrent users
- Many simultaneous requests
- Database queries are slow

**When to Decrease:**
- Low memory available (< 512MB)
- Memory usage is high
- Out of connections errors

```bash
# Increase for high traffic
railway variables set SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
railway redeploy

# Decrease for low memory
railway variables set SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=5
railway redeploy
```

**Memory Impact:**
- Each connection ≈ 5-10MB RAM
- 10 connections = 50-100MB
- Consider when setting

---

### SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE
**Purpose:** Minimum idle connections kept open

**Type:** Integer  
**Default:** `2`  
**Recommended Range:** `1-5`  
**Example:** `2`

**Higher = Faster Initial Response**
- Keeps connections ready to use
- Slightly more memory usage
- Better for consistent high traffic

**Lower = Lower Memory Usage**
- Creates connections on demand
- Slightly slower initial response
- Better for bursty traffic

```bash
# Increase for consistent traffic
railway variables set SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
railway redeploy

# Decrease for memory savings
railway variables set SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=1
railway redeploy
```

---

## Security Settings

### spring.security.password.bcrypt.rounds
**Purpose:** BCrypt password hashing strength

**Type:** Integer  
**Default:** `10`  
**Recommended:** `10-12`  
**Example:** `10`

**Higher = Slower Password Hashing**
- More secure against brute force
- Takes longer to hash (good for security)
- Current setting: reasonable balance

**⚠️ Important:**
- Set in `application.properties`
- Don't change after users created
- Higher values slow login by milliseconds

---

### server.ssl.enabled
**Purpose:** Enable HTTPS/SSL

**Type:** Boolean  
**Default:** `false` (Railway handles SSL)  
**Example:** `false`

**⚠️ Important:**
- Railway provides automatic HTTPS
- Set to `false` (don't handle in app)
- Let Railway's reverse proxy handle SSL
- Reduces load on application

---

## Logging Configuration

### LOGGING_LEVEL_ROOT
**Purpose:** Root logger level

**Type:** String  
**Values:** `OFF` | `ERROR` | `WARN` | `INFO` | `DEBUG` | `TRACE`  
**Default:** `INFO`  
**Example:** `INFO`

**Log Levels Explained:**

| Level | Description | Use Case |
|-------|-------------|----------|
| `OFF` | No logging | Production only |
| `ERROR` | Only errors | Minimal logging |
| `WARN` | Warnings + errors | Production |
| `INFO` | Normal info | Default |
| `DEBUG` | Detailed debugging | Troubleshooting |
| `TRACE` | Very detailed | Deep debugging |

**Set Log Level:**
```bash
# Normal (production)
railway variables set LOGGING_LEVEL_ROOT=INFO
railway redeploy

# Troubleshooting
railway variables set LOGGING_LEVEL_ROOT=DEBUG
railway redeploy

# Minimal
railway variables set LOGGING_LEVEL_ROOT=WARN
railway redeploy
```

---

### LOGGING_LEVEL_COM_CHRISWATNEE_MARTINIS
**Purpose:** Martinis application logger level

**Type:** String  
**Default:** `INFO`  
**Example:** `DEBUG`

**Set for Detailed App Logging:**
```bash
railway variables set LOGGING_LEVEL_COM_CHRISWATNEE_MARTINIS=DEBUG
railway redeploy

# View detailed logs
railway logs -f
```

---

### LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB
**Purpose:** Spring Web framework logger

**Type:** String  
**Default:** `INFO`  
**Example:** `DEBUG`

**Use for HTTP Request Debugging:**
```bash
railway variables set LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG
railway redeploy
```

---

## Setting Variables

### Via Railway Dashboard

**Step-by-step:**

1. Go to [Railway Dashboard](https://railway.app)
2. Select project
3. Select **martinis** service (not database)
4. Click **"Variables"** tab
5. Click **"New Variable"** button
6. Enter key and value
7. Click **"Add"**
8. Railway auto-redeploys

**Visual Guide:**
```
Project Dashboard
  ↓
Select Service (martinis)
  ↓
Variables Tab
  ↓
New Variable
  ↓
Key: ADMIN_USERNAME
Value: admin
  ↓
Add
  ↓
Deployment starts...
```

### Via Railway CLI

**Set Single Variable:**
```bash
railway variables set KEY=value
```

**Examples:**
```bash
railway variables set ADMIN_USERNAME=admin
railway variables set ADMIN_PASSWORD="MySecure!Pass123"
railway variables set SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
```

**Set Multiple Variables:**
```bash
railway variables set \
  ADMIN_USERNAME=admin \
  ADMIN_PASSWORD="MySecure!Pass123" \
  ADMIN_FIRSTNAME=John \
  ADMIN_LASTNAME=Doe
```

**View All Variables:**
```bash
railway variables
```

**View Specific Variable:**
```bash
railway variables | grep ADMIN_USERNAME
```

**Delete Variable:**
```bash
railway variables unset VARIABLE_NAME
```

---

## Validation Checklist

### Before First Deployment

- [ ] **ADMIN_USERNAME** set
  ```bash
  railway variables | grep ADMIN_USERNAME
  ```

- [ ] **ADMIN_PASSWORD** set and secure
  ```bash
  railway variables | grep ADMIN_PASSWORD
  # Verify it's not: password, admin123, martinis, etc.
  ```

- [ ] **ADMIN_FIRSTNAME** set
  ```bash
  railway variables | grep ADMIN_FIRSTNAME
  ```

- [ ] **ADMIN_LASTNAME** set
  ```bash
  railway variables | grep ADMIN_LASTNAME
  ```

- [ ] **DATABASE_URL** automatically set
  ```bash
  railway variables | grep DATABASE_URL
  # Should output mysql://user:pass@host/db
  ```

- [ ] **SQL_INIT_MODE** set to "always"
  ```bash
  railway variables | grep SQL_INIT_MODE
  ```

### After Successful Deployment

- [ ] Application starts without errors
  ```bash
  railway logs -f | head -20
  ```

- [ ] Can login with admin credentials
  - Visit app URL
  - Login with admin / password

- [ ] Database tables created
  ```bash
  railway connect mysql
  mysql> SHOW TABLES;
  # Should see: user, project, scene, actor, etc.
  ```

- [ ] Change ADMIN_PASSWORD after first login
  - Use admin panel
  - Create strong new password

---

## Common Configuration Scenarios

### Development Environment

```bash
railway variables set \
  ADMIN_USERNAME=dev \
  ADMIN_PASSWORD="DevPass123!Test" \
  ADMIN_FIRSTNAME=Dev \
  ADMIN_LASTNAME=User \
  LOGGING_LEVEL_ROOT=DEBUG \
  LOGGING_LEVEL_COM_CHRISWATNEE_MARTINIS=DEBUG \
  SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=5
```

### Production Environment

```bash
railway variables set \
  ADMIN_USERNAME=admin \
  ADMIN_PASSWORD="VerySecure!Pass@2025#Prod" \
  ADMIN_FIRSTNAME=Production \
  ADMIN_LASTNAME=Admin \
  LOGGING_LEVEL_ROOT=INFO \
  SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=15 \
  SQL_INIT_MODE=never
```

### High Traffic Environment

```bash
railway variables set \
  SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=25 \
  SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5 \
  LOGGING_LEVEL_ROOT=WARN \
  SQL_INIT_MODE=never
```

---

## Troubleshooting Variables

### "Environment Variable Not Taking Effect"

**Solutions:**
1. Verify variable is set: `railway variables`
2. Wait for deployment to complete: `railway logs -f`
3. Check variable name spelling (case-sensitive)
4. Hard redeploy: `railway service restart martinis`

### "Can't Login After Setting Password"

**Solutions:**
```bash
# 1. Verify password is set
railway variables | grep ADMIN_PASSWORD

# 2. Check logs for initialization errors
railway logs -f | grep -i "password\|user\|admin"

# 3. If admin user not created, reset:
railway connect mysql
mysql> DELETE FROM user WHERE username='admin';
mysql> DELETE FROM authority WHERE username='admin';
# Redeploy to recreate

# 4. Set SQL_INIT_MODE=always and redeploy
railway variables set SQL_INIT_MODE=always
railway redeploy
```

### "Database Connection Fails"

**Solutions:**
```bash
# 1. Verify DATABASE_URL is set
railway variables | grep DATABASE_URL

# 2. If empty, MySQL service not linked
railway status
# Should show both 'martinis' and 'mysql'

# 3. If MySQL missing, add it
railway add --database mysql
railway redeploy
```

---

## Security Best Practices

1. **Never commit passwords to Git**
   - Use `.env` locally (in `.gitignore`)
   - Use Railway environment variables for production

2. **Use strong admin password**
   - Minimum 16 characters
   - Mix uppercase, lowercase, numbers, symbols
   - No dictionary words or patterns

3. **Change default admin password after login**
   - Initial password is temporary
   - Login → Settings → Change Password

4. **Use different passwords for different environments**
   - Development ≠ Production
   - Staging ≠ Production

5. **Rotate passwords periodically**
   - Every 90 days recommended
   - After team member leaves
   - After suspected breach

---

## Reference Quick Links

- **Railway Docs:** [docs.railway.app/deploy/environment-variables](https://docs.railway.app)
- **Spring Boot Docs:** [spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
- **HikariCP Docs:** [github.com/brettwooldridge/HikariCP](https://github.com/brettwooldridge/HikariCP)

---

**Your environment is configured! Ready to deploy? 🚀**
