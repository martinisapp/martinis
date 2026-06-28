---
name: mysql-database
description: MySQL database schema, access patterns, and MCP configuration for the Martinis screenplay management system. Use when querying, modifying, or troubleshooting the database.
---

# MySQL Database - Martinis

## MCP Server

The MySQL MCP server provides read-only SQL access to the database. It is configured in `.devin/config.json` using `@modelcontextprotocol/server-mysql`.

### Connection

The database URL is provided via the `DATABASE_URL` environment variable:
- **Railway (production):** `mysql://user:password@host:port/railway` (auto-provided)
- **Local (Docker Compose):** `mysql://martinis_user:password@localhost:3306/martinis`

### Available MCP Tools

- **query** - Execute read-only SQL (SELECT, SHOW, DESCRIBE, EXPLAIN)
- **Table schemas** - Auto-discovery of tables, columns, types, and constraints

## Database Schema

### Tables

| Table | Purpose |
|-------|---------|
| `user` | Application users (login, auth) |
| `authority` | User roles (ROLE_USER, ROLE_ADMIN) |
| `project` | Screenplay projects |
| `scene` | Scenes within a project (ordered) |
| `person` | Characters in a project |
| `actor` | Physical performers |
| `block` | Dialogue/action content within scenes (ordered, bookmarkable) |

### Relationships

```
project (1) --> (*) scene
project (1) --> (*) person
scene   (1) --> (*) block
person  (1) --> (*) block (nullable)
actor   (1) --> (*) person (nullable)
user    (1) --> (*) authority (via username)
```

### Key Columns

**user**: id, username (unique, varchar 20), password (bcrypt hash), enabled, first_name, last_name
**authority**: username (FK → user.username), authority (e.g. ROLE_ADMIN)
**project**: id, title (varchar 100)
**scene**: id, `order` (int), name, project_id (FK, CASCADE delete)
**actor**: id, first_name, last_name, phone, email
**person**: id, name, full_name, actor_id (FK, SET NULL), project_id (FK, CASCADE delete)
**block**: id, `order` (int), content (TEXT), person_id (FK, SET NULL), scene_id (FK, CASCADE delete), is_bookmarked (tinyint)

## Common Queries

```sql
-- List all projects
SELECT * FROM project;

-- Get scenes for a project (ordered)
SELECT * FROM scene WHERE project_id = ? ORDER BY `order`;

-- Get blocks in a scene with character names
SELECT b.*, p.name AS person_name
FROM block b
LEFT JOIN person p ON b.person_id = p.id
WHERE b.scene_id = ?
ORDER BY b.`order`;

-- Get all characters in a project with their actors
SELECT p.*, a.first_name AS actor_first, a.last_name AS actor_last
FROM person p
LEFT JOIN actor a ON p.actor_id = a.id
WHERE p.project_id = ?;

-- Get bookmarked blocks
SELECT b.*, s.name AS scene_name, p.name AS person_name
FROM block b
JOIN scene s ON b.scene_id = s.id
LEFT JOIN person p ON b.person_id = p.id
WHERE b.is_bookmarked = 1 AND s.project_id = ?;
```

## Local Development Setup

1. Start MySQL via Docker Compose: `docker-compose up -d mysql`
2. Copy `.devin/config.local.json.example` to `.devin/config.local.json`
3. Update credentials in `.devin/config.local.json`

## Configuration (DatabaseConfig.java)

The app uses `DatabaseConfig.java` which:
- Detects Railway's `mysql://` URL format and converts to JDBC
- Extracts credentials from the URL automatically
- Configures HikariCP with optimized pool settings (max 10, min idle 2)
- Enables prepared statement caching and batch rewriting
