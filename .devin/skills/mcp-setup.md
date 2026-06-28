---
name: mcp-setup
description: How to set up and use MCP (Model Context Protocol) servers with the Martinis project. Use when configuring AI tool integrations or troubleshooting MCP connectivity.
---

# MCP Setup - Martinis

## Overview

This project uses the Model Context Protocol (MCP) to provide AI assistants with direct, read-only access to the MySQL database. This enables intelligent queries, schema inspection, and data exploration without manual SQL execution.

## Configuration Files

| File | Purpose | Committed |
|------|---------|-----------|
| `.devin/config.json` | Shared MCP server config (uses env vars) | Yes |
| `.devin/config.local.json` | Personal credentials override | No (gitignored) |
| `.devin/config.local.json.example` | Template for local setup | Yes |

## Setup for Devin Web Platform

1. Navigate to **Settings > Connections > MCP servers** in Devin
2. Install the **MySQL** MCP server from the marketplace (`cognition-mysql`)
3. Configure with your Railway `DATABASE_URL` or local MySQL connection string

## Setup for Devin CLI / Local AI Tools

1. Copy the example config:
   ```bash
   cp .devin/config.local.json.example .devin/config.local.json
   ```

2. Edit `.devin/config.local.json` with your local MySQL credentials:
   ```json
   {
     "mcpServers": {
       "mysql": {
         "command": "npx",
         "args": ["-y", "@modelcontextprotocol/server-mysql", "mysql://user:pass@localhost:3306/martinis"]
       }
     }
   }
   ```

3. Verify the MCP server can connect:
   ```bash
   npx -y @modelcontextprotocol/server-mysql mysql://user:pass@localhost:3306/martinis
   ```

## Environment Variables

The MCP server requires `DATABASE_URL` in MySQL URI format:
```
mysql://username:password@hostname:port/database_name
```

**Railway:** This is automatically provided when a MySQL service is linked.
**Local Docker:** Default is `mysql://martinis_user:<password>@localhost:3306/martinis`

## Security Notes

- The MCP server provides **read-only** access (SELECT, SHOW, DESCRIBE, EXPLAIN only)
- All queries run within READ ONLY transactions
- Never commit credentials to `.devin/config.local.json` (it is gitignored)
- For production databases, use a read-only database user if possible

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused | Ensure MySQL is running: `docker-compose up -d mysql` |
| Access denied | Check credentials in your DATABASE_URL |
| Server not found | Run `npm install -g @modelcontextprotocol/server-mysql` or use `npx` |
| Timeout | Check network/firewall settings for Railway connections |
