-- Martinis Database Setup for Railway
-- This script creates all tables and initial data
-- Run this in your Railway MySQL database (default name: railway)

-- Use the Railway default database
USE railway;

-- Drop existing tables if they exist (in correct order due to foreign keys)
DROP TABLE IF EXISTS `block`;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS scene;
DROP TABLE IF EXISTS actor;
DROP TABLE IF EXISTS project;

-- Create tables
CREATE TABLE `user` (
	id int NOT NULL AUTO_INCREMENT,
	username varchar(20) NOT NULL,
	`password` varchar(100) NOT NULL,
	enabled tinyint(1) NOT NULL,
	first_name varchar(30) NOT NULL,
	last_name varchar(30) NOT NULL,
	PRIMARY KEY (id),
	KEY username(username)
);

CREATE TABLE authority (
	username varchar(20) NOT NULL,
	authority varchar(20) NOT NULL,
	KEY username(username),
	FOREIGN KEY (username) REFERENCES `user`(username)
);

CREATE TABLE project (
	id int NOT NULL AUTO_INCREMENT,
	title varchar(100) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE scene (
	id int NOT NULL AUTO_INCREMENT,
	`order` int NOT NULL,
	`name` varchar(255) NOT NULL,
	project_id int NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE actor (
	id int NOT NULL AUTO_INCREMENT,
	first_name varchar(30) NOT NULL,
	last_name varchar(30) NULL,
	phone varchar(20) NULL,
	email varchar(30) NULL,
	PRIMARY KEY (id)
);

CREATE TABLE person (
	id int NOT NULL AUTO_INCREMENT,
	`name` varchar(60) NOT NULL,
	full_name varchar(60) NOT NULL,
	actor_id int NULL,
	project_id int NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (actor_id) REFERENCES actor(id) ON DELETE SET NULL,
	FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE `block` (
	id int NOT NULL AUTO_INCREMENT,
	`order` int NOT NULL,
	content TEXT NOT NULL,
	person_id int NULL,
	scene_id int NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE SET NULL,
	FOREIGN KEY (scene_id) REFERENCES scene(id) ON DELETE CASCADE
);

-- Insert initial users
-- Password for both users is: password
INSERT INTO `user` (id, username, `password`, enabled, first_name, last_name) VALUES
(1, 'admin', '$2a$10$BGmcUaHCftiGRKB2d8XCWO2u04qbdsVt.xoq1gw1f2ILA6TaRpA2G', 1, 'Chris', 'Watnee'),
(2, 'clint', '$2a$10$BGmcUaHCftiGRKB2d8XCWO2u04qbdsVt.xoq1gw1f2ILA6TaRpA2G', 1, 'Clint', 'Watnee');

-- Insert user authorities
INSERT INTO authority (username, authority) VALUES
('admin', 'ROLE_ADMIN'),
('admin', 'ROLE_USER'),
('clint', 'ROLE_USER');

-- Verify tables were created
SELECT 'Database setup complete!' AS status;
SHOW TABLES;
