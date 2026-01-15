-- Enhanced Database Setup with Advanced AI Features

USE auis_db;

-- Add new tables for advanced AI features

-- Action Sequences Table - For Next-Step Prediction

CREATE TABLE IF NOT EXISTS action_sequences (
    sequence_id INT PRIMARY KEY AUTO_INCREMENT,
    first_action VARCHAR(50) NOT NULL,
    next_action VARCHAR(50) NOT NULL,
    occurrence_count INT DEFAULT 1,
    confidence_score FLOAT DEFAULT 0.0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (first_action, next_action)
);


-- Anomaly Detection Table - Track suspicious patterns

CREATE TABLE IF NOT EXISTS anomalies (
    anomaly_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    anomaly_type VARCHAR(50),
    action_count INT,
    time_window_seconds INT,
    severity_level VARCHAR(20),
    detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


-- User Habits Table - For Time-Series Intelligence

CREATE TABLE IF NOT EXISTS user_habits (
    habit_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    peak_hour INT,
    preferred_action VARCHAR(50),
    frequency_score INT,
    last_detected TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


-- User Similarity Table - For Collaborative Filtering

CREATE TABLE IF NOT EXISTS user_similarity (
    similarity_id INT PRIMARY KEY AUTO_INCREMENT,
    user_a INT NOT NULL,
    user_b INT NOT NULL,
    similarity_score FLOAT,
    shared_actions INT,
    last_calculated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_a) REFERENCES users(user_id),
    FOREIGN KEY (user_b) REFERENCES users(user_id)
);


-- Insert sample data for action sequences

INSERT INTO action_sequences (first_action, next_action, occurrence_count, confidence_score) VALUES
('LOGIN', 'BROWSE', 45, 0.85),
('LOGIN', 'SEARCH', 35, 0.75),
('BROWSE', 'PURCHASE', 28, 0.72),
('SEARCH', 'BROWSE', 40, 0.80),
('PURCHASE', 'LOGIN', 15, 0.50)
ON DUPLICATE KEY UPDATE occurrence_count = occurrence_count + 1;

SELECT 'Database v2 setup completed successfully!' AS status;


