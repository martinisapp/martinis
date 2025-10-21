# Martinis

A Java Spring MVC application for managing film production scenes, actors, and blocking.

## Deployment on Railway

This application is configured to run on [Railway](https://railway.com/).

### Prerequisites

- A Railway account
- A MySQL database service on Railway

### Deployment Steps

1. **Create a new project on Railway**
   - Go to [railway.com](https://railway.com/) and create a new project
   - Add a MySQL database service to your project

2. **Connect your repository**
   - Connect this GitHub repository to Railway
   - Railway will automatically detect the Java/Maven configuration

3. **Configure environment variables**
   - Railway will automatically set `PORT` and `DATABASE_URL`
   - The application supports the following database URL formats:
     - `DATABASE_URL` (Railway default)
     - `MYSQL_URL` (Railway MySQL)
     - `JAWSDB_URL` (Heroku compatibility)

4. **Deploy**
   - Railway will automatically build and deploy using the configuration in:
     - `nixpacks.toml` - Build configuration
     - `railway.json` - Deployment settings
     - `Procfile` - Start command

### Database Setup

After deployment, you'll need to initialize your MySQL database with the required schema. You can find the SQL scripts in the `sql/` directory.

### Heroku Deployment

This application is also compatible with Heroku:

1. Create a new Heroku app
2. Add JawsDB MySQL addon
3. Deploy using Git or GitHub integration
4. The `Procfile` will be used to start the application

### Local Development

For local development with MySQL:

1. Uncomment the local datasource configuration in `src/main/resources/spring-persistence.xml`
2. Update the connection details to match your local MySQL instance
3. Run with Maven: `mvn clean package && java -jar target/dependency/webapp-runner.jar target/*.war`
