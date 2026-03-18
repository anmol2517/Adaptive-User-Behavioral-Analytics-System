# 🏢 RevWorkforce — HRM Microservices System

> A scalable, production-ready **Human Resource Management (HRM)** system built with a microservices architecture using **Spring Boot**, **Spring Cloud**, **MySQL**, and **Docker**.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Overview](#api-overview)
- [Docker Setup](#docker-setup)
- [Team](#team)

---

## 🧭 Overview

**RevWorkforce** is a group project that implements a full-featured HRM platform using a microservices-based design. It handles everything from employee onboarding, leave management, and performance reviews to notifications and HR reporting — all behind a single **API Gateway** with **JWT-based authentication**.

Key highlights:
- Role-based access control (`ADMIN`, `HR`, `MANAGER`, `Employee`)
- Centralized configuration via Spring Cloud Config Server
- Service registration & discovery via Eureka
- Fully containerized with Docker Compose

---

## 🏗️ Architecture

```
                        ┌─────────────────────────────────┐
                        │         React/Angular UI         │
                        └───────────────┬─────────────────┘
                                        │
                        ┌───────────────▼─────────────────┐
                        │           API Gateway            │
                        │    (Port 8080 | JWT Auth)        │
                        └──┬──────┬──────┬──────┬──────┬──┘
                           │      │      │      │      │
               ┌───────────┘  ┌───┘  ┌───┘  ┌───┘  ┌───┘
               ▼              ▼      ▼      ▼      ▼
          User-Svc   Employee-Svc  Leave  Perf  Notify  Report
               │              │      │      │      │      │
               └──────────────┴──────┴──────┘      │      │
                                   │                └──────┘
                              ┌────▼────┐
                              │  MySQL  │
                              └─────────┘

        ┌──────────────────────────────────────────────┐
        │  Eureka Service Discovery  |  Config Server   │
        └──────────────────────────────────────────────┘
```

---

## 🔧 Microservices

| Service | Port | Description |
|---|---|---|
| **api-gateway** | `8080` | Single entry point, JWT validation, routing |
| **service-discovery** | `8761` | Eureka service registry |
| **config-server** | `8888` | Centralized configuration management |
| **user-service** | — | Authentication, user management, JWT issuance |
| **employee-service** | — | Employee profiles, departments, designations |
| **leave-service** | — | Leave application, approval, balance tracking |
| **performance-service** | — | Self-reviews, manager feedback, goals |
| **notification-service** | — | In-app notifications for employees |
| **reporting-service** | — | HR dashboard and analytics |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot, Spring Cloud |
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Config Management | Spring Cloud Config |
| Security | Spring Security + JWT |
| Database | MySQL 8.0 |
| Containerization | Docker, Docker Compose |
| Build Tool | Maven |

---

## 📁 Project Structure

```
Revworkforce-Microservices-P3/
├── api-gateway/               # API Gateway service
├── config-server/             # Centralized config server
├── service-discovery/         # Eureka server
├── user-service/              # Authentication & user management
├── employee-service/          # Employee, department, designation APIs
├── leave-service/             # Leave management
├── performance-service/       # Performance reviews & goals
├── notification-service/      # Notification system
├── reporting-service/         # HR reporting & dashboard
├── revworkforce-ui            # Frontend UI
├── docker-compose.yml         # Full stack Docker setup
├── Dockerfile.template        # Shared Dockerfile for all services
├── pom.xml                    # Root Maven POM
├── .env.example               # Sample environment variables
└── api_documentation.md       # Detailed API reference
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0 (or use the Docker container)

### Run with Docker (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/Harshita188/Revworkforce-Microservices-P3.git
cd Revworkforce-Microservices-P3

# 2. Copy and configure the environment file
cp .env.example .env
# Edit .env and set your JWT_SECRET and other values

# 3. Start all services
docker-compose up --build
```

Once running, the API Gateway will be available at: **http://localhost:8080**  
Eureka Dashboard: **http://localhost:8761**

### Run Locally (Manual)

```bash
# Start services in this order:
# 1. MySQL
# 2. service-discovery (Eureka)
# 3. config-server
# 4. api-gateway
# 5. All other microservices

cd service-discovery && mvn spring-boot:run
cd config-server && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd user-service && mvn spring-boot:run
# ... and so on for each service
```

---

## 🔑 Environment Variables

Create a `.env` file at the project root (see `.env.example`):

```env
JWT_SECRET=your_super_secret_jwt_key_here
DB_USERNAME=root
DB_PASSWORD=root
```

All services pick up configuration from the **Config Server** at startup.

---

## 📡 API Overview

All requests go through the **API Gateway** at `http://localhost:8080`.  
A valid **JWT Bearer token** (obtained via login) is required for protected endpoints.

### Authentication

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/auth/login` | Login and receive JWT token | Public |
| PUT | `/auth/change-password` | Change user password | Authenticated |

### User Management

| Method | Endpoint | Description | Role |
|---|---|---|---|
| POST | `/users` | Create new user | ADMIN |
| GET | `/users` | List all users | ADMIN |
| GET | `/users/{id}` | Get user by ID | Authenticated |

### Employee Management

| Method | Endpoint | Description | Role |
|---|---|---|---|
| POST | `/employees` | Create employee profile | ADMIN / HR |
| GET | `/employees` | List all employees | Authenticated |
| GET | `/employees/{id}` | Get employee by ID | Authenticated |
| PUT | `/employees/{id}` | Update employee | ADMIN / HR |
| DELETE | `/employees/{id}` | Delete employee | ADMIN |
| GET | `/employees/count` | Total employee count | Authenticated |

### Leave Management

| Method | Endpoint | Description | Role |
|---|---|---|---|
| POST | `/api/leaves/apply` | Apply for leave | Authenticated |
| POST | `/api/leaves/approve` | Approve/reject leave | ADMIN / MANAGER |
| GET | `/api/leaves/history/{employeeId}` | Leave history | Authenticated |
| GET | `/api/leaves/balances/{employeeId}` | Leave balances | Authenticated |
| POST | `/api/leaves/initialize/{employeeId}` | Init leave balances | ADMIN |

### Performance Management

| Method | Endpoint | Description | Role |
|---|---|---|---|
| POST | `/api/performance/self-review` | Submit self-review | Authenticated |
| POST | `/api/performance/manager-feedback/{reviewId}` | Manager feedback | MANAGER / ADMIN |
| GET | `/api/performance/history/{employeeId}` | Review history | Authenticated |
| POST | `/api/performance/goals` | Add goal | MANAGER / ADMIN |
| GET | `/api/performance/goals/{employeeId}` | Get employee goals | Authenticated |

### Notifications

| Method | Endpoint | Description | Role |
|---|---|---|---|
| POST | `/api/notifications/send` | Send notification | Internal |
| GET | `/api/notifications/{employeeId}` | Get notifications | Authenticated |

### Reporting

| Method | Endpoint | Description | Role |
|---|---|---|---|
| GET | `/api/reports/dashboard` | HR Dashboard data | ADMIN |

> 📄 For full request/response schemas, see [api_documentation.md](./api_documentation.md)

---

## 🐳 Docker Setup

The `docker-compose.yml` orchestrates all services with proper startup ordering:

```
MySQL → Eureka (service-discovery) → Config Server → API Gateway → All Microservices
```

Services communicate internally using Docker's network. The API Gateway is the only publicly exposed service on port `8080`.

---

## 👥 Team

This is a group project built as part of the **Revature** training program.

| Contributor | GitHub |
|---|---|
| Harshita | [@Harshita188](https://github.com/Harshita188) |
| *(Add teammates here)* | — |

---

## 📄 License

This project is for educational purposes as part of a training program.

---

> **RevWorkforce** — Empowering HR with modern microservices. 🚀
