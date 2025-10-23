# Martinis - Professional Screenplay Management System

A modern Spring Boot 3.2.0 web application for managing screenplay projects, scenes, actors, and shooting schedules. Optimized for deployment on Railway.com.

## Features

- **Project Management** - Create and manage multiple screenplay projects
- **Scene Organization** - Organize scenes with detailed metadata and scheduling
- **Actor Database** - Maintain a comprehensive actor database
- **Character Management** - Track characters and their relationships to projects
- **Block Management** - Manage screenplay blocks/lines with bookmarking
- **File Upload** - Import screenplay documents (Word format)
- **RESTful API** - Complete REST API for all features
- **Role-Based Security** - User and admin roles with Spring Security
- **Responsive Design** - JSP-based views optimized for web

## Technology Stack

- **Framework:** Spring Boot 3.2.0
- **Java Version:** 17 (LTS)
- **Build Tool:** Maven 3.9
- **Database:** MySQL 8.0+ (with HikariCP connection pooling)
- **Security:** Spring Security with BCrypt password hashing
- **View Technology:** JSP with JSTL
- **Server:** Embedded Tomcat
- **Cloud Platform:** Railway.com (optimized)

## Quick Deploy to Railway

[![Deploy on Railway](https://railway.com/button.svg)](https://railway.com/template/https://github.com/martinisapp/martinis)

Click the button above to deploy to Railway. The deployment will automatically:
1. Create a new Railway project
2. Provision a MySQL 8.0 database with auto-generated credentials
3. Deploy the Martinis application
4. Connect the app to the database automatically

### Prerequisites

- A [Railway.com](https://railway.com) account (free tier available)
- GitHub account (for repository connection)

### Deployment Steps

1. **Click "Deploy on Railway"**
   - Click the button above
   - Sign in to Railway (or create a free account)
   - Authorize GitHub access if prompted

2. **Automatic Setup**
   Railway automatically:
   - Creates a new project with two services:
     - **martinis** - The Spring Boot application
     - **mysql** - MySQL 8.0 database
   - Generates secure database credentials
   - Connects the app to the database via `DATABASE_URL`
   - Builds and deploys the application
   - Initializes the database schema

3. **Configure Admin User (Optional)**

   By default, an admin user is created with username `admin` and a random password.
   To set a custom admin password:

   - Go to your Railway project
   - Click on the **martinis** service
   - Go to **Variables** tab
   - Add or modify:
     ```bash
     ADMIN_PASSWORD=your-secure-password-here
     ```
   - The app will automatically redeploy

4. **Access Your Application**
   - Railway provides a public URL: `https://your-app.railway.app`
   - Click "Generate Domain" if no domain is assigned yet
   - Visit the URL and log in with your admin credentials
   - Default username: `admin`

### Detailed Documentation

For comprehensive deployment instructions, troubleshooting, and configuration details, see:

**[RAILWAY_MYSQL_SETUP.md](./RAILWAY_MYSQL_SETUP.md)** - Automatic MySQL setup guide for Railway
**[RAILWAY.md](./RAILWAY.md)** - Complete Railway deployment guide

## Local Development

### Option 1: Docker Setup (Recommended)

The easiest way to run the application locally is using Docker Compose, which automatically sets up MySQL and the application.

#### Prerequisites

- Docker 20.10 or higher
- Docker Compose 2.0 or higher

#### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/martinisapp/martinis.git
   cd martinis
   ```

2. **Configure environment (optional)**
   ```bash
   # Copy the example environment file
   cp .env.example .env

   # Edit .env to customize settings (optional)
   # Default credentials: admin/admin123
   ```

3. **Start the application**
   ```bash
   # Start MySQL and the application
   docker-compose up -d

   # View logs
   docker-compose logs -f app
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - Login with: `admin` / `admin123` (or your configured credentials)

5. **Optional: Start phpMyAdmin for database management**
   ```bash
   # Start with phpMyAdmin
   docker-compose --profile tools up -d

   # Access phpMyAdmin at http://localhost:8081
   ```

#### Docker Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Rebuild after code changes
docker-compose up -d --build

# View logs
docker-compose logs -f app
docker-compose logs -f mysql

# Reset database (WARNING: deletes all data)
docker-compose down -v
docker-compose up -d

# Access MySQL CLI
docker-compose exec mysql mysql -u martinis_user -p martinis
# Password: martinis_pass (or your configured password)
```

### Option 2: Manual Setup

If you prefer to run components separately or don't have Docker available.

#### Prerequisites

- Java 17 or higher
- Maven 3.9 or higher
- MySQL 8.0 or higher (or access to Railway development database)

#### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/martinisapp/martinis.git
   cd martinis
   ```

2. **Configure local database**

   Option 1 - Local MySQL:
   ```bash
   # Create database
   mysql -u root -p
   CREATE DATABASE martinis;
   EXIT;

   # Set environment variables
   export DATABASE_URL="jdbc:mysql://localhost:3306/martinis?useSSL=false&serverTimezone=UTC"
   export ADMIN_USERNAME="admin"
   export ADMIN_PASSWORD="password"
   ```

   Option 2 - Use Railway development database:
   ```bash
   railway link
   railway run mvn spring-boot:run
   ```

3. **Build and run**
   ```bash
   # Build
   mvn clean package

   # Run
   java -jar target/martinis.jar

   # Or use Maven Spring Boot plugin
   mvn spring-boot:run
   ```

4. **Access the application**
   - Open browser to `http://localhost:8080`
   - Log in with your admin credentials

### Development with Hot Reload

```bash
# Add Spring Boot DevTools dependency (already in pom.xml)
mvn spring-boot:run
```

## Configuration Files

### Railway Configuration

- **`railway.json`** - Railway service configuration with health checks
- **`nixpacks.toml`** - Build configuration with JVM optimization
- **`Procfile`** - Process definition for Railway
- **`app.json`** - Application manifest with environment variables
- **`.railwayignore`** - Files to exclude from deployment

### Application Configuration

- **`application.properties`** - Spring Boot configuration
- **`DatabaseConfig.java`** - Database connection and HikariCP setup
- **`SecurityConfig.java`** - Spring Security configuration
- **`schema.sql`** - Database schema (auto-initialized)

## Architecture

### Application Layers

```
┌──────────────────────────────────────┐
│     Controllers (Web & REST)          │
│  HomeController, ProjectController,   │
│  SceneController, ActorController,    │
│  BlockController, PersonController    │
├──────────────────────────────────────┤
│    Services (Business Logic)          │
│  ProjectService, SceneService,        │
│  ActorService, BlockService,          │
│  PersonService                        │
├──────────────────────────────────────┤
│    DAOs (Data Access Layer)           │
│  ProjectDao, SceneDao, ActorDao,      │
│  BlockDao, PersonDao                  │
├──────────────────────────────────────┤
│   HikariCP Connection Pool            │
├──────────────────────────────────────┤
│      MySQL Database                   │
└──────────────────────────────────────┘
```

### Database Schema

- **`user`** - User accounts with BCrypt passwords
- **`authority`** - User roles (ROLE_USER, ROLE_ADMIN)
- **`project`** - Screenplay projects
- **`scene`** - Scenes within projects
- **`actor`** - Actor database
- **`person`** - Characters in projects
- **`block`** - Screenplay blocks/lines with bookmarking

## API Endpoints

### Projects
- `GET /api/projects` - List all projects
- `GET /api/projects/{id}` - Get project details
- `POST /api/projects` - Create project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Scenes
- `GET /api/projects/{projectId}/scenes` - List scenes
- `GET /api/scenes/{id}` - Get scene details
- `POST /api/scenes` - Create scene
- `PUT /api/scenes/{id}` - Update scene
- `DELETE /api/scenes/{id}` - Delete scene

### Actors
- `GET /api/actors` - List all actors
- `GET /api/actors/{id}` - Get actor details
- `POST /api/actors` - Create actor
- `PUT /api/actors/{id}` - Update actor
- `DELETE /api/actors/{id}` - Delete actor

### Blocks
- `GET /api/blocks` - List all blocks
- `GET /api/blocks/{id}` - Get block details
- `POST /api/blocks` - Create block
- `PUT /api/blocks/{id}` - Update block
- `DELETE /api/blocks/{id}` - Delete block

## Security

### Authentication
- Form-based login with Spring Security
- BCrypt password hashing (strength: 10)
- Session-based authentication

### Authorization
- **Public:** `/`, `/login`, `/register`, `/dbcheck`, static resources
- **User:** All authenticated features
- **Admin:** `/admin/**` routes

## Performance Optimizations

### JVM Settings
```bash
-Xmx768m -Xms256m
-XX:+UseContainerSupport
-XX:MaxRAMPercentage=80.0
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100
```

### Database Connection Pool (HikariCP)
- Maximum pool size: 10
- Minimum idle: 2
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes
- Max lifetime: 30 minutes

### Web Optimizations
- HTTP/2 enabled
- Response compression (gzip)
- Static resource caching
- Prepared statement caching

## Troubleshooting

### Common Issues

**Database Connection Errors**
- Verify `DATABASE_URL` is set correctly
- Check MySQL service is running on Railway
- Review logs: `railway logs`

**Build Failures**
- Ensure Java 17 is installed
- Clear Maven cache: `mvn clean`
- Verify `pom.xml` is valid

**Login Issues**
- Ensure admin user environment variables are set
- Check password was hashed correctly (BCrypt)
- Review security logs in Railway dashboard

For detailed troubleshooting, see [RAILWAY.md](./RAILWAY.md#monitoring--troubleshooting)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

- **Documentation:** [RAILWAY.md](./RAILWAY.md)
- **Issues:** [GitHub Issues](https://github.com/martinisapp/martinis/issues)
- **Railway Support:** [Railway Discord](https://discord.gg/railway)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Changelog

### Version 2.0.0 - Complete Railway Rewrite (October 2025)

- Complete rewrite of Railway deployment configuration
- Upgraded to Spring Boot 3.2.0
- Migrated from Spring MVC 4.3 to Spring Boot 3.2
- Java 17 support with optimized JVM settings
- Enhanced database configuration with SSL support
- PostgreSQL support alongside MySQL
- Improved security with BCrypt password hashing
- HikariCP connection pooling optimizations
- Comprehensive Railway deployment documentation
- Performance optimizations for production
- HTTP/2 and response compression
- Auto-schema initialization on first deployment

---

**Made with Spring Boot 3.2.0 | Optimized for Railway.com**
