# Railway Deployment (Consolidated)

This file is intentionally short to avoid duplicate Railway instructions.

## Use these docs

1. **Start here:** [README.md → Quick Deploy to Railway](./README.md#quick-deploy-to-railway)
2. **Full deployment/troubleshooting:** [RAILWAY.md](./RAILWAY.md)
3. **MySQL-specific notes:** [RAILWAY_MYSQL_SETUP.md](./RAILWAY_MYSQL_SETUP.md)

## Current deploy method

- Deploy from GitHub repo in Railway dashboard
- Add a MySQL service in the same Railway project
- Set admin environment variables
- Railway builds from `Dockerfile` via `railway.json`
