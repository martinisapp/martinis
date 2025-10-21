-- Initial data for Railway MySQL
-- This will auto-run after schema.sql when the application starts
-- Uses INSERT IGNORE to skip if data already exists, preserving data across deployments

-- Insert initial users (only if they don't exist)
-- Password for both users is: password
INSERT IGNORE INTO `user` (id, username, `password`, enabled, first_name, last_name) VALUES
(1, 'admin', '$2a$10$Ts1Tw69gBUEcTYx2L4exouFA9jFguHWiGBOPctoNHXxLK3k7X9a3W', 1, 'Chris', 'Watnee'),
(2, 'clint', '$2a$10$Ts1Tw69gBUEcTYx2L4exouFA9jFguHWiGBOPctoNHXxLK3k7X9a3W', 1, 'Clint', 'Watnee');

-- Insert user authorities (only if they don't exist)
INSERT IGNORE INTO authority (username, authority) VALUES
('admin', 'ROLE_ADMIN'),
('admin', 'ROLE_USER'),
('clint', 'ROLE_USER');
