# Login Troubleshooting for Railway Deployment

## No Default Users

For security reasons, this application no longer creates default users. You must create your own admin user before you can log in.

## Creating Your First Admin User

### Option 1: Using Environment Variables (Recommended)

Set the following environment variables in Railway before deployment:

1. Go to Railway → Your App Service → Variables
2. Add these variables:
   - `ADMIN_USERNAME` - Your desired admin username (e.g., "admin")
   - `ADMIN_PASSWORD` - Your desired admin password (use a strong password!)
   - `ADMIN_FIRSTNAME` - (Optional) Admin's first name
   - `ADMIN_LASTNAME` - (Optional) Admin's last name

3. Redeploy the application

The application will automatically create the admin user on startup if these environment variables are set and the user doesn't already exist.

### Option 2: Manual Database Insert

If you prefer to create users manually, connect to your Railway MySQL database and run:

```sql
-- Create a user (replace values with your own)
INSERT INTO `user` (username, `password`, enabled, first_name, last_name)
VALUES ('yourusername', 'BCRYPT_HASH_HERE', 1, 'First', 'Last');

-- Grant user authorities
INSERT INTO authority (username, authority) VALUES
('yourusername', 'ROLE_ADMIN'),
('yourusername', 'ROLE_USER');
```

**Important**: You need to generate a BCrypt hash for your password. You can use the BcryptTest.java utility or online BCrypt generators.

## Step 1: Verify Database Contents

Go to Railway → Your MySQL database → Query tab, and run:

```sql
-- Check if users exist
SELECT id, username, LEFT(`password`, 20) as password_start, enabled, first_name, last_name
FROM `user`;

-- Check if authorities exist
SELECT * FROM authority;
```

### Expected Results:

You should see at least one user with ROLE_USER or ROLE_ADMIN authority.

If you see **no users**, you need to create an admin user (see above).

## Step 2: Check Database Connection

Visit `/dbcheck` on your deployed application (e.g., `https://your-app.railway.app/dbcheck`)

This page will show:
- All users in the database
- All authorities
- Database statistics
- Instructions for creating users if none exist

## Step 3: Login Credentials

When logging in, enter:
- **Username**: The username you created (plain text)
- **Password**: The password you set (plain text)

The application will automatically hash your typed password and compare it to the BCrypt hash stored in the database.

**IMPORTANT**: Do NOT type the bcrypt hash! Enter your actual password in plain text.

## Step 4: Check for Errors

If login fails, check Railway logs for any errors containing:
- `SQLException`
- `Authentication failed`
- `Bad credentials`

Copy and paste any errors you find.

## Common Issues

1. **No users created**: The most common issue - make sure to create an admin user first
2. **Wrong environment variables**: Check that ADMIN_USERNAME and ADMIN_PASSWORD are set correctly
3. **Old deployment**: Railway might not have deployed the latest version yet
4. **Wrong database**: The MYSQL_URL might be pointing to the wrong database
5. **Incorrect password**: BCrypt is case-sensitive - make sure you're typing the exact password you set

## Next Steps

If you've created a user but still can't log in, enable debug logging to see why Spring Security is rejecting the credentials. Check the Railway deployment logs for detailed authentication error messages.
