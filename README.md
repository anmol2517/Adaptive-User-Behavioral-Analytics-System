# 🚀 Adaptive-User-Behavioral-Analytics-System
### ✨ Adaptive Intelligence (AI) ✅ || Artificial Intelligence (AI) ❌

> An enterprise-grade, AI-inspired, rule-based Java console application for intelligent user behavior analysis, session tracking, anomaly detection, and security enforcement.

---

## 📌 Overview

Adaptive Intelligence (AI) is a smart, behavior-driven system designed to analyze user actions, learn patterns, predict next steps, detect anomalies, and generate meaningful insights — without using machine learning.

The system simulates **real-world enterprise analytics platforms**.

---

## 🎯 Objectives

- Understand user behavior patterns
- Predict next possible actions
- Track user sessions
- Detect suspicious activities
- Provide personalized insights
- Generate persistent reports
- Ensure account-level security
- Handle failures gracefully
- Bridge the gap between simple CRUD systems and intelligent analytics platforms

---

## 🧠 Key Capabilities

- 📊 Data Analytics
- 🔁 Pattern Learning
- 🧭 Action Prediction
- 🧑‍💻 Session Management
- 🚨 Anomaly Detection
- 🔐 Security System
- 🧾 Report Generation
- 🧵 Multithreading
- 🛡️ Fault Tolerance

---

## 🏗️ Tech Stack

| Layer | Technology |
|------|------------|
| Language | Java (Core Java) |
| Database | MySQL |
| Connectivity | JDBC |
| Architecture | Layered (MVC) |
| Logging | File-based Logging |
| Threading | Java Executor Service |
| Security | SHA-256 Hashing |
| Reports | TXT-based persistent reports |

---

## 📂 Folder Structure

```
src/
├── com/auis/main/
│   └── MainApp.java
├── com/auis/controller/
│   └── UserController.java
├── com/auis/service/
│   ├── IntelligenceService.java
│   └── PredictiveAnalyticsEngine.java
├── com/auis/dao/
│   ├── UserDAO.java
│   ├── ActivityDAO.java
│   ├── RuleDAO.java
│   ├── ActionSequenceDAO.java
│   ├── AnomalyDAO.java
│   ├── UserHabitDAO.java
│   ├── UserSimilarityDAO.java
│   └── UserSessionDAO.java
├── com/auis/model/
│   ├── User.java
│   ├── UserActivity.java
│   ├── Rule.java
│   ├── ActionSequence.java
│   ├── Anomaly.java
│   ├── UserHabit.java
│   ├── UserSimilarity.java
│   └── UserSession.java
├── com/auis/util/
│   ├── DBConnection.java
│   ├── SystemLogger.java
│   ├── SessionManager.java
│   ├── HealthMonitor.java
│   ├── SystemReport.java
│   ├── ExceptionHandler.java
│   └── SecurityManager.java
└── com/auis/worker/
    └── BackgroundIntelligenceWorker.java

scripts/
├── setup_database.sql
├── setup_database_v2.sql
└── setup_database_v3.sql

logs/
├── system.log
└── system_report_*.txt
```

---

## ⚙️ Installation & Setup

### Prerequisites

- Java 8+
- MySQL 5.7+
- MySQL Connector/J

---

### Step 1: Database Setup

```sql
SOURCE scripts/setup_database.sql;
SOURCE scripts/setup_database_v2.sql;
SOURCE scripts/setup_database_v3.sql;
```

---

### Step 2: Configure Database

Edit:

```
src/com/auis/util/DBConnection.java
```

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/auis_db";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";
```

---

### Step 3: Compile

```bash
javac -cp .:mysql-connector-java-8.0.33.jar -d bin src/com/auis/**/*.java
```

---

### Step 4: Run

```bash
java -cp bin:mysql-connector-java-8.0.33.jar com.auis.main.MainApp
```

---

## 🧩 Features

### 1️⃣ Persistent Reports

- Saves reports to disk
- User activity reports
- Security reports
- Anomaly reports

---

### 2️⃣ Centralized Exception Handling

- No crashes
- Friendly error messages
- Stack trace logging

---

### 3️⃣ Session Tracking

- Login time
- Logout time
- Session duration
- Action count

---

### 4️⃣ Self-Learning Patterns

- Action sequence tracking
- Confidence score update
- Smart predictions

---

### 5️⃣ Anomaly Detection

- Bot detection
- Rapid action detection
- Severity scoring

---

### 6️⃣ Security System

- Password hashing (SHA-256)
- Brute-force prevention
- Auto-lock system
- Auto-unlock

---

## 🔐 Security Features

- SQL Injection prevention
- Password hashing
- Account locking
- Bot detection
- Audit logging

---

## 📊 Logging & Reports

All logs saved in:

```
logs/system.log
logs/system_report_*.txt
```

---

## 🚀 Production Readiness

- Thread-safe
- Fault tolerant
- Scalable
- Transaction-safe
- Secure

---

## 🧪 Testing Scenarios
- **Security Test:** Enter wrong password 3 times to check **Account Lock** feature.
- **Bot Detection:** Perform more than 10 actions within 10 seconds to trigger **Critical Anomaly**.
- **Prediction Test:** Perform "Login" then "Search" multiple times; the system will start predicting "Search" as your next action.
- **Report Test:** Check the `logs/` folder after running the app to see generated `.txt` reports.

---

## 📌 Project Status

✅ Completed  
✅ Tested  
✅ Production Ready  
✅ Enterprise Grade  
---
