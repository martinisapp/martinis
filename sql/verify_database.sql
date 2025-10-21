-- Database Verification Script for Railway MySQL
-- Run this in Railway's MySQL console to verify database setup

-- Check if tables exist
SHOW TABLES;

-- Check user table structure
DESCRIBE `user`;

-- Check authority table structure
DESCRIBE authority;

-- List all users
SELECT id, username, enabled, first_name, last_name FROM `user`;

-- List all authorities
SELECT username, authority FROM authority;

-- Count total users
SELECT COUNT(*) as total_users FROM `user`;

-- Count total authorities
SELECT COUNT(*) as total_authorities FROM authority;
