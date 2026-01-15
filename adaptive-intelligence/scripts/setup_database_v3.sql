USE auis_db;


-- Step 1: Optimization Indexes

CREATE INDEX idx_user_act ON user_activity(user_id, activity_time);
CREATE INDEX idx_seq_start ON action_sequences(first_action);


-- Step 2: FEATURE #3 (User Sessions Table)

CREATE TABLE IF NOT EXISTS user_sessions (
    session_id VARCHAR(100) PRIMARY KEY,
    user_id INT NOT NULL,
    session_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    session_end_time TIMESTAMP NULL,
    total_actions INT DEFAULT 0,
    session_duration_minutes INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_session (user_id)
);


-- Step 3: FEATURE #6 (Users Table Updates)


ALTER TABLE users 
ADD COLUMN password_hash VARCHAR(255) DEFAULT '12345',
ADD COLUMN is_locked BOOLEAN DEFAULT FALSE,
ADD COLUMN failed_attempts INT DEFAULT 0,
ADD COLUMN locked_until TIMESTAMP NULL;


-- Step 4: FEATURE #5 (Anomalies Table Updates)

ALTER TABLE anomalies 
ADD COLUMN is_resolved BOOLEAN DEFAULT FALSE,
ADD COLUMN anomaly_score INT DEFAULT 0,
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;


-- Step 5: Anomaly Indexes

CREATE INDEX idx_severity ON anomalies (severity_level);
CREATE INDEX idx_user_anomaly ON anomalies (user_id, severity_level);


-- Step 6: Test Data Update

UPDATE users SET password_hash = 'password123' WHERE username = 'anmol';

