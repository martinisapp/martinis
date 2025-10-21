# Martinis

A Spring MVC web application for managing screenplay projects, scenes, actors, and shooting schedules.

## Deployment

### Railway.com

This application is configured for deployment on Railway.com.

#### Prerequisites
- A Railway account (https://railway.app)
- A MySQL database (Railway provides MySQL as an add-on)

#### Deployment Steps

1. **Create a new project on Railway**
   - Visit https://railway.app and create a new project
   - Connect your GitHub repository

2. **Add a MySQL database**
   - In your Railway project, click "New Service"
   - Select "Database" → "MySQL"
   - Railway will automatically create a `MYSQL_URL` environment variable

3. **Deploy the application**
   - Railway will automatically detect the Java/Maven project
   - The build process is configured in `nixpacks.toml`
   - The app will start using the command specified in `railway.json`

4. **Environment Variables**
   - The application automatically reads the `MYSQL_URL` environment variable provided by Railway
   - The application also supports `DATABASE_URL` and `JAWSDB_URL` for compatibility
   - No manual configuration needed for the database connection

5. **Create Initial Admin User**

   **IMPORTANT**: For security reasons, no default users are created. You must create an admin user before you can log in.

   Set the following environment variables in Railway:
   - `ADMIN_USERNAME` - Your desired admin username (e.g., "admin")
   - `ADMIN_PASSWORD` - Your desired admin password (use a strong password!)
   - `ADMIN_FIRSTNAME` - (Optional) Admin's first name
   - `ADMIN_LASTNAME` - (Optional) Admin's last name

   The application will automatically create the admin user on startup.

   **Security Best Practice**: After the first successful deployment and user creation, remove the `ADMIN_PASSWORD` environment variable to prevent unauthorized access.

6. **Database Setup**
   - The database schema is automatically initialized on first deployment
   - SQL scripts are available in the `sql/` directory for reference

#### Alternative: One-Click Deploy

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/new)

### Heroku (Legacy)

The application also supports Heroku deployment using the `JAWSDB_URL` environment variable.

## Local Development

### Prerequisites
- Java 8 or higher
- Maven 3.x
- MySQL database

### Database Configuration

For local development, uncomment and configure the local database bean in `src/main/resources/spring-persistence.xml`:

```xml
<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/martinis?serverTimezone=EST"/>
    <property name="username" value="root"/>
    <property name="password" value="rootroot"/>
    <property name="initialSize" value="5"/>
    <property name="maxTotal" value="10"/>
</bean>
```

### Build and Run

```bash
# Build the project
mvn clean package

# Run locally (requires webapp-runner)
java -jar target/dependency/webapp-runner.jar target/*.war
```

## Technology Stack

- **Framework**: Spring MVC 4.3.18
- **Database**: MySQL with JDBC
- **Build Tool**: Maven
- **Server**: Tomcat (via webapp-runner)
- **Java Version**: 8

## Features

- Project management for screenplays
- Scene organization and scheduling
- Actor/character management
- Shooting schedule blocks
- RESTful web services API
