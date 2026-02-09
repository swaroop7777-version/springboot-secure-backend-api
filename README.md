# Spring Boot Secure Backend API (JWT + PostgreSQL + AWS S3)

Production-style backend application built with Spring Boot, featuring secure REST APIs, JWT authentication, PostgreSQL persistence, and AWS S3 file upload support. Designed with clean layered architecture and Docker-ready deployment.

## Features
- ✅ RESTful APIs with clean layered architecture (Controller → Service → Repository → DTO)
- 🔐 Authentication & Authorization using Spring Security + JWT
- 👥 Role-based access control (RBAC)
- 🗄️ PostgreSQL + Spring Data JPA (migrations included via Flyway)
- ☁️ AWS S3 integration for profile image/file upload (multipart upload support)
- 🧪 Unit + integration testing (Testcontainers for PostgreSQL)
- 🐳 Docker support for consistent dev/deploy environments

## Tech Stack
- Java 17
- Spring Boot
- Spring Security, JWT
- PostgreSQL, Spring Data JPA
- Flyway Migrations
- AWS SDK (S3)
- Docker
- Maven, JUnit, Testcontainers

## Project Structure (High-Level)
## Getting Started

### 1) Prerequisites
- Java 17
- Maven
- PostgreSQL (or Docker)
- AWS credentials (only needed if testing S3 uploads)

### 2) Configure Environment
Update `src/main/resources/application.yml` with:
- PostgreSQL connection details
- JWT secret / expiration
- AWS S3 bucket name + region (if using S3)

Example values to set:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `aws.region`
- `aws.s3.bucket`

### 3) Run the Application
```bash
mvn spring-boot:run
