-- Database Verification Script for Railway MySQL
-- Run this in Railway's MySQL console to verify users were created correctly

-- Check if tables exist
SHOW TABLES;

-- Check user table structure
DESCRIBE `user`;

-- Check authority table structure
DESCRIBE authority;

-- Check if admin user exists
SELECT * FROM `user` WHERE username = 'admin';

-- Check if clint user exists
SELECT * FROM `user` WHERE username = 'clint';

-- Check authorities for admin
SELECT * FROM authority WHERE username = 'admin';

-- Check authorities for clint
SELECT * FROM authority WHERE username = 'clint';

-- Count total users
SELECT COUNT(*) as total_users FROM `user`;

-- Count total authorities
SELECT COUNT(*) as total_authorities FROM authority;
