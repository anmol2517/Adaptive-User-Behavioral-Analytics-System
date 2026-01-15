-- Database Setup for Adaptive User Intelligence System (AUIS)

CREATE DATABASE IF NOT EXISTS auis_db;
USE auis_db;


-- 1. Users Table - Store user information

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- 2. User Activity Table - Track user behavior

CREATE TABLE IF NOT EXISTS user_activity (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    action VARCHAR(50) NOT NULL,
    activity_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


-- 3. AI Rules Table - Store decision rules

CREATE TABLE IF NOT EXISTS ai_rules (
    rule_id INT PRIMARY KEY AUTO_INCREMENT,
    condition_text VARCHAR(100) NOT NULL,
    suggestion_text VARCHAR(150) NOT NULL
);


-- Insert sample data

INSERT INTO users (username) VALUES ('anmol');
INSERT INTO users (username) VALUES ('priya');

INSERT INTO ai_rules (condition_text, suggestion_text) VALUES
('REPEAT_ACTION', 'You often perform this action. Try the next recommended step.'),
('INACTIVE_USER', 'You have been inactive. Explore new features.'),
('HIGH_FREQUENCY', 'This action is performed frequently. Consider automating it.');


-- Display tables

SHOW TABLES;
SELECT * FROM users;
SELECT * FROM ai_rules;

