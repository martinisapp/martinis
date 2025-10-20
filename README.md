# Martinis

A Spring MVC application for screenplay management.

## Deployment

### Railway.com Deployment

This application is compatible with Railway. To deploy:

1. Create a new project on [Railway](https://railway.com)
2. Connect your GitHub repository
3. Add a MySQL database to your project
4. Railway will automatically detect the Java application and build it using the `nixpacks.toml` configuration
5. The application will use the `DATABASE_URL` environment variable provided by Railway's MySQL service

Required environment variables:
- `DATABASE_URL` or `MYSQL_URL` - Automatically provided by Railway MySQL service
- `PORT` - Automatically provided by Railway

### Heroku Deployment

This application is also compatible with Heroku:

1. Create a new Heroku app
2. Add JawsDB MySQL addon
3. Deploy using Git or GitHub integration
4. The `Procfile` will be used to start the application