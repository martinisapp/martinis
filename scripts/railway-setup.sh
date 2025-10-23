#!/bin/bash
# Railway MySQL Automatic Setup Script
# This script helps you set up MySQL and configure environment variables for Railway deployment

set -e

echo "========================================="
echo "Railway MySQL Automatic Setup"
echo "========================================="
echo ""

# Check if railway CLI is installed
if ! command -v railway &> /dev/null; then
    echo "❌ ERROR: Railway CLI is not installed"
    echo ""
    echo "To install Railway CLI:"
    echo "  npm install -g @railway/cli"
    echo "  or visit: https://docs.railway.app/develop/cli"
    echo ""
    exit 1
fi

echo "✓ Railway CLI is installed"
echo ""

# Check if user is logged in
if ! railway whoami &> /dev/null; then
    echo "You need to log in to Railway first."
    echo ""
    railway login
fi

echo "✓ Logged in to Railway"
echo ""

# Check if project is linked
if ! railway status &> /dev/null; then
    echo "This directory is not linked to a Railway project."
    echo ""
    read -p "Would you like to link an existing project? (y/n): " link_choice
    if [ "$link_choice" = "y" ] || [ "$link_choice" = "Y" ]; then
        railway link
    else
        read -p "Would you like to create a new project? (y/n): " create_choice
        if [ "$create_choice" = "y" ] || [ "$create_choice" = "Y" ]; then
            railway init
        else
            echo "Please link or create a project first:"
            echo "  railway link   # Link existing project"
            echo "  railway init   # Create new project"
            exit 1
        fi
    fi
fi

echo "✓ Project is linked"
echo ""

# Check if MySQL is already added
echo "Checking for MySQL database..."
if railway variables | grep -q "DATABASE_URL"; then
    echo "✓ MySQL database is already configured"
else
    echo "⚠️  MySQL database not found"
    echo ""
    read -p "Would you like to add MySQL now? (y/n): " mysql_choice
    if [ "$mysql_choice" = "y" ] || [ "$mysql_choice" = "Y" ]; then
        echo "Adding MySQL to your Railway project..."
        railway add mysql || echo "Note: You may need to add MySQL manually via the Railway dashboard"
        echo ""
        echo "MySQL added! DATABASE_URL will be automatically configured."
    else
        echo "Please add MySQL to your project:"
        echo "  railway add mysql"
        echo "  or use the Railway dashboard: New → Database → Add MySQL"
        exit 1
    fi
fi

echo ""
echo "========================================="
echo "Environment Variables Configuration"
echo "========================================="
echo ""

# Check current variables
echo "Current environment variables:"
railway variables
echo ""

# Configure admin user
echo "Let's configure your admin user credentials."
echo ""

read -p "Admin username (default: admin): " admin_username
admin_username=${admin_username:-admin}

read -s -p "Admin password: " admin_password
echo ""
if [ -z "$admin_password" ]; then
    echo "❌ ERROR: Admin password cannot be empty"
    exit 1
fi

read -s -p "Confirm admin password: " admin_password_confirm
echo ""
if [ "$admin_password" != "$admin_password_confirm" ]; then
    echo "❌ ERROR: Passwords do not match"
    exit 1
fi

read -p "Admin first name (default: Admin): " admin_firstname
admin_firstname=${admin_firstname:-Admin}

read -p "Admin last name (default: User): " admin_lastname
admin_lastname=${admin_lastname:-User}

echo ""
echo "Setting environment variables..."

# Set variables
railway variables set ADMIN_USERNAME="$admin_username"
railway variables set ADMIN_PASSWORD="$admin_password"
railway variables set ADMIN_FIRSTNAME="$admin_firstname"
railway variables set ADMIN_LASTNAME="$admin_lastname"
railway variables set SQL_INIT_MODE="always"

echo ""
echo "✓ Environment variables configured successfully!"
echo ""

echo "========================================="
echo "Setup Complete!"
echo "========================================="
echo ""
echo "Your Railway project is now configured with:"
echo "  ✓ MySQL database (DATABASE_URL set automatically)"
echo "  ✓ Admin user: $admin_username"
echo "  ✓ Admin name: $admin_firstname $admin_lastname"
echo "  ✓ SQL initialization: enabled"
echo ""
echo "Next steps:"
echo "  1. Deploy your application:"
echo "     git add ."
echo "     git commit -m 'Configure Railway deployment'"
echo "     git push"
echo ""
echo "  2. Monitor the deployment:"
echo "     railway logs"
echo ""
echo "  3. Open your application:"
echo "     railway open"
echo ""
echo "The database schema will be automatically initialized on first startup."
echo ""
echo "For detailed documentation, see: RAILWAY_MYSQL_SETUP.md"
echo ""
