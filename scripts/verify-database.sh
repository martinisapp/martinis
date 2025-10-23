#!/bin/bash
# Database Connection Verification Script for Railway
# This script verifies that the DATABASE_URL is properly configured

set -e

echo "========================================="
echo "Railway MySQL Connection Verification"
echo "========================================="
echo ""

# Check if DATABASE_URL is set
if [ -z "$DATABASE_URL" ]; then
    echo "❌ ERROR: DATABASE_URL environment variable is not set"
    echo ""
    echo "To fix this issue:"
    echo "  1. Add MySQL to your Railway project:"
    echo "     railway add --mysql"
    echo "  2. Redeploy your application"
    echo ""
    exit 1
fi

echo "✓ DATABASE_URL is set"

# Parse the DATABASE_URL to extract components
# Expected format: mysql://user:password@host:port/database
if [[ $DATABASE_URL =~ mysql://([^:]+):([^@]+)@([^:]+):([^/]+)/(.+) ]]; then
    DB_USER="${BASH_REMATCH[1]}"
    DB_HOST="${BASH_REMATCH[3]}"
    DB_PORT="${BASH_REMATCH[4]}"
    DB_NAME="${BASH_REMATCH[5]}"

    echo "✓ DATABASE_URL format is valid"
    echo ""
    echo "Database Connection Details:"
    echo "  Host: $DB_HOST"
    echo "  Port: $DB_PORT"
    echo "  Database: $DB_NAME"
    echo "  User: $DB_USER"
    echo "  Password: ****"
    echo ""
elif [[ $DATABASE_URL =~ jdbc:mysql://([^:]+):([^/]+)/([^?]+) ]]; then
    DB_HOST="${BASH_REMATCH[1]}"
    DB_PORT="${BASH_REMATCH[2]}"
    DB_NAME="${BASH_REMATCH[3]}"

    echo "✓ JDBC DATABASE_URL format detected"
    echo ""
    echo "Database Connection Details:"
    echo "  Host: $DB_HOST"
    echo "  Port: $DB_PORT"
    echo "  Database: $DB_NAME"
    echo ""
else
    echo "❌ ERROR: DATABASE_URL format is invalid"
    echo "   Current value: ${DATABASE_URL:0:30}..."
    echo ""
    echo "Expected format: mysql://user:password@host:port/database"
    echo "             or: jdbc:mysql://host:port/database"
    echo ""
    exit 1
fi

# Check for admin user environment variables
echo "========================================="
echo "Admin User Configuration"
echo "========================================="
echo ""

if [ -z "$ADMIN_USERNAME" ]; then
    echo "⚠️  WARNING: ADMIN_USERNAME is not set"
    echo "   You will need to manually create an admin user"
else
    echo "✓ ADMIN_USERNAME is set: $ADMIN_USERNAME"
fi

if [ -z "$ADMIN_PASSWORD" ]; then
    echo "⚠️  WARNING: ADMIN_PASSWORD is not set"
    echo "   You will need to manually create an admin user"
else
    echo "✓ ADMIN_PASSWORD is set: ****"
fi

if [ -z "$ADMIN_FIRSTNAME" ]; then
    echo "⚠️  WARNING: ADMIN_FIRSTNAME is not set (will use default)"
else
    echo "✓ ADMIN_FIRSTNAME is set: $ADMIN_FIRSTNAME"
fi

if [ -z "$ADMIN_LASTNAME" ]; then
    echo "⚠️  WARNING: ADMIN_LASTNAME is not set (will use default)"
else
    echo "✓ ADMIN_LASTNAME is set: $ADMIN_LASTNAME"
fi

echo ""
echo "========================================="
echo "Spring Boot Configuration"
echo "========================================="
echo ""

if [ -z "$PORT" ]; then
    echo "⚠️  WARNING: PORT is not set (will use default: 8080)"
else
    echo "✓ PORT is set: $PORT"
fi

SQL_INIT_MODE="${SQL_INIT_MODE:-always}"
echo "✓ SQL_INIT_MODE: $SQL_INIT_MODE"

echo ""
echo "========================================="
echo "Verification Complete"
echo "========================================="
echo ""
echo "Next steps:"
echo "  1. The application will automatically connect to MySQL"
echo "  2. Schema will be initialized on first startup (if SQL_INIT_MODE=always)"
echo "  3. Admin user will be created if credentials are provided"
echo ""
echo "To view application logs:"
echo "  railway logs"
echo ""
