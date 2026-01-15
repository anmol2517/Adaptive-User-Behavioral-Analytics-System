# Complete Setup Guide for Adaptive Intelligence

## Prerequisites
- Java 8 or higher
- MySQL Server (5.7 or higher)
- MySQL Connector/J (JDBC driver)

## Step-by-Step Setup

### Step 1: Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Run the SQL scripts
source scripts/setup_database.sql;
source scripts/setup_database_v2.sql;
source scripts/setup_database_v3.sql;

# Verify tables created
use auis_db;
show tables;
```

### Step 2: Update Credentials
Edit `src/com/auis/util/DBConnection.java`:
```java
private static final String DB_USER = "root";           // Your MySQL username
private static final String DB_PASSWORD = "password";   // Your MySQL password
private static final String DB_URL = "jdbc:mysql://localhost:3306/auis_db";
```

### Step 3: Compile the Project
```bash
# Download MySQL JDBC Driver
# From: https://dev.mysql.com/downloads/connector/j/

# Compile all Java files
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/**/*.java

# Alternatively, compile individual packages
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/util/*.java
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/model/*.java
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/dao/*.java
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/service/*.java
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/worker/*.java
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/controller/*.java
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/main/*.java
```

### Step 4: Run the Application
```bash
java -cp bin:mysql-connector-java-8.0.33.jar com.auis.main.MainApp
```

## Troubleshooting

### Issue: "MySQL JDBC Driver not found"
**Solution**: Ensure mysql-connector-java JAR is in the classpath
```bash
# Check if file exists
ls -la mysql-connector-java-8.0.33.jar

# Add to classpath correctly
java -cp bin:./mysql-connector-java-8.0.33.jar com.auis.main.MainApp
```

### Issue: "Connection refused"
**Solution**: Verify MySQL is running
```bash
# Linux/Mac
sudo systemctl status mysql

# Windows (Command Prompt as Administrator)
net start MySQL80

# Or check if running on correct port
netstat -an | grep 3306
```

### Issue: "Database auis_db not found"
**Solution**: Run the SQL setup scripts
```bash
mysql -u root -p auis_db < scripts/setup_database.sql
mysql -u root -p auis_db < scripts/setup_database_v2.sql
mysql -u root -p auis_db < scripts/setup_database_v3.sql
```

### Issue: "Access denied for user 'root'@'localhost'"
**Solution**: Update credentials in DBConnection.java
```java
private static final String DB_PASSWORD = "your_actual_password";
```

## File Structure After Setup
```
project/
├── src/
│   └── com/auis/  (all Java source files)
├── bin/           (compiled .class files - created after compile)
├── logs/          (system.log - created at runtime)
├── scripts/
│   ├── setup_database.sql
│   ├── setup_database_v2.sql
│   └── setup_database_v3.sql
├── mysql-connector-java-8.0.33.jar
├── README.md
└── SETUP_GUIDE.md
```

## Running a Test Session

1. **Start the application**
   ```bash
   java -cp bin:mysql-connector-java-8.0.33.jar com.auis.main.MainApp
   ```

2. **Login/Register**
   - Enter option `1`
   - Enter username: `test_user`

3. **Record Actions**
   - Enter option `1`
   - Type actions: `LOGIN`, `BROWSE`, `SEARCH`, `PURCHASE`
   - Repeat some actions multiple times

4. **View Suggestions**
   - Enter option `2`
   - System shows AI insights, habits, and recommendations

5. **View Activity Report**
   - Enter option `3`
   - See detailed activity analysis

6. **Check System Health**
   - Enter option `4` (or `2` if not logged in)
   - View performance metrics

## Logging

Logs are saved to `logs/system.log`:
```bash
# View all logs
tail -f logs/system.log

# View only errors
grep ERROR logs/system.log

# View only security alerts
grep SECURITY logs/system.log

# View performance metrics
grep PERFORMANCE logs/system.log
```

## Database Verification

```bash
# Connect to database
mysql -u root -p auis_db

# Check all tables
SHOW TABLES;

# Check sample data
SELECT * FROM users;
SELECT * FROM user_activity LIMIT 10;
SELECT * FROM action_sequences;

# Check logs table (if enabled)
SELECT COUNT(*) FROM user_activity;
```

## Advanced Configuration

### Change Background Worker Interval
Edit `BackgroundIntelligenceWorker.java`:
```java
private final int SCAN_INTERVAL_MS = 30000;  // Change to 60000 for 60 seconds
```

### Adjust Anomaly Detection Threshold
Edit `PredictiveAnalyticsEngine.java`:
```java
if (secondsElapsed < 10) {  // Change 10 to your threshold
```

### Change Database Connection Pool
Edit `DBConnection.java` for connection pooling (optional):
```java
// Can add HikariCP or other pooling library
```
---
