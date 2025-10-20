# Railway Deployment Guide

This guide will walk you through deploying the Martinis application on Railway.

## Step 1: Create a Railway Account

1. Go to [railway.app](https://railway.app/)
2. Click "Login" and sign up using GitHub (recommended for easy repository access)
3. Authorize Railway to access your GitHub account

## Step 2: Create a New Project

1. Once logged in, click **"New Project"**
2. You'll see several options for creating a project

## Step 3: Add MySQL Database

1. In the new project, click **"+ New"**
2. Select **"Database"**
3. Choose **"MySQL"**
4. Railway will automatically provision a MySQL database
5. The database will automatically set the following environment variables:
   - `MYSQL_URL`
   - `DATABASE_URL`
   - `MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`

## Step 4: Deploy Your Application

### Option A: Deploy from GitHub (Recommended)

1. In your Railway project, click **"+ New"**
2. Select **"GitHub Repo"**
3. Search for and select the **`martinisapp/martinis`** repository
4. Select the branch: **`claude/railway-deployment-config-011CUJuhiQ5wvMyetJxZiqmi`** (or merge to main and deploy from main)
5. Click **"Deploy"**

Railway will automatically:
- Detect it's a Java/Maven project
- Use the `nixpacks.toml` configuration
- Run `mvn clean package -DskipTests`
- Start the application with the `Procfile` command

### Option B: Deploy with Railway CLI

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Link to your project (run in the martinis directory)
railway link

# Deploy
railway up
```

## Step 5: Initialize the Database

After deployment, you need to set up your database schema:

1. In Railway, click on your **MySQL database**
2. Go to the **"Data"** tab or **"Connect"** tab
3. Note the connection details

### Option A: Using MySQL Workbench or CLI

Connect to your Railway MySQL database and run the SQL scripts from the `sql/` directory.

### Option B: Using Railway CLI

```bash
# Connect to your database
railway connect mysql

# Then run your SQL scripts
source /path/to/your/sql/schema.sql
```

## Step 6: Configure Your Application

1. Click on your application service (not the database)
2. Go to the **"Variables"** tab
3. Verify that `PORT` is set (Railway sets this automatically)
4. The application will automatically use `DATABASE_URL` from the MySQL service

## Step 7: Access Your Application

1. In your application service, go to the **"Settings"** tab
2. Scroll to **"Networking"**
3. Click **"Generate Domain"** to get a public URL
4. Your application will be available at: `https://your-app-name.railway.app`

## Troubleshooting

### Build Fails

- Check the **"Deployments"** tab for build logs
- Common issues:
  - Maven dependencies not downloading: Check network/timeout settings
  - Java version mismatch: The project uses Java 8 (configured in nixpacks.toml)

### Application Won't Start

- Check the **"Deployments"** tab and click on the latest deployment to see logs
- Verify `DATABASE_URL` is set correctly
- Ensure the MySQL service is running

### Database Connection Errors

- Verify the MySQL service is running and healthy
- Check that the application service can access the database
- In Railway, services in the same project can communicate automatically

### View Application Logs

1. Click on your application service
2. Go to the **"Deployments"** tab
3. Click on the active deployment
4. View real-time logs in the **"Logs"** section

## Environment Variables Reference

Railway automatically provides:
- `PORT` - The port your application should listen on
- `DATABASE_URL` - MySQL connection URL (from MySQL service)
- `MYSQL_URL` - Alternative MySQL connection URL

The application is configured to use these automatically.

## Updating Your Application

To deploy updates:

1. Push changes to your GitHub repository
2. Railway will automatically detect changes and redeploy
3. You can disable auto-deploy in Settings > Service > Auto Deploy

Or manually trigger deployment:
1. Go to **"Deployments"** tab
2. Click **"Deploy"** button

## Useful Railway CLI Commands

```bash
# View logs
railway logs

# Open your app in browser
railway open

# Run commands in your service environment
railway run <command>

# Connect to database
railway connect mysql

# View environment variables
railway variables
```

## Additional Resources

- [Railway Documentation](https://docs.railway.app/)
- [Railway Discord Community](https://discord.gg/railway)
- [Railway Status](https://status.railway.app/)

## Cost Considerations

- Railway offers a free tier with $5 credit per month
- After free credits, you'll be charged based on usage
- MySQL database and web service both consume resources
- Monitor usage in the Railway dashboard under "Usage"
