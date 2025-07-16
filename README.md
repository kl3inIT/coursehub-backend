# 🎓 CourseHub Backend

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9.5+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> **A comprehensive backend service for online course management platform built with Spring Boot**

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [Project Structure](#-project-structure)
- [Development](#-development)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)

## 🌟 Overview

CourseHub Backend is a robust Spring Boot application that provides RESTful APIs for an online learning platform. It handles course management, user authentication, payment processing, analytics, and real-time communications through WebSocket.

## ✨ Features

- 🔐 **Authentication & Authorization** - JWT-based auth with OAuth2 Google integration
- 👥 **User Management** - Student, instructor, and admin role management
- 📚 **Course Management** - CRUD operations for courses, lessons, and modules
- 💳 **Payment Processing** - SePay integration with TPBank
- 📊 **Analytics Dashboard** - Course performance and user engagement metrics
- 🏷️ **Category & Discount Management** - Dynamic course categorization and promotions
- 💬 **Real-time Communication** - WebSocket support for comments and notifications
- 📧 **Email Service** - Gmail SMTP integration for OTP verification
- ☁️ **Cloud Storage** - AWS S3 integration (ap-southeast-2 region)
- 📈 **Monitoring** - Spring Actuator health checks and metrics
- 🔒 **SSL/TLS** - Let's Encrypt certificates with auto-renewal
- 🌐 **Reverse Proxy** - Nginx configuration for production

## 🛠 Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.4.5 |
| **Database** | MySQL 8.0 |
| **Cache** | Redis Latest |
| **Security** | Spring Security, JWT, OAuth2 (Google) |
| **Build Tool** | Maven 3.9.5+ |
| **Cloud Storage** | AWS S3 (ap-southeast-2) |
| **Email** | Gmail SMTP |
| **Payment** | SePay + TPBank |
| **Reverse Proxy** | Nginx |
| **SSL/TLS** | Let's Encrypt (Certbot) |
| **Containerization** | Docker, Docker Compose |

## 📋 Prerequisites

Before running this application, make sure you have:

- ☕ **Java 21** or higher
- 🛠️ **Maven 3.9.5** or higher
- 🗄️ **MySQL 8.0** or higher
- 🔄 **Redis** (for caching)
- 🐳 **Docker** & **Docker Compose** (recommended)

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/kl3inIT/coursehub-backend.git
cd coursehub-backend

# 2. Create environment file
cp .env.example .env
# Edit .env with your configuration

# 3. Start all services
docker-compose up -d --build

# 4. Verify services are running
docker ps

# 5. Access the application
# API: http://localhost:8080
# MySQL: localhost:3307 (root/123456)
# Redis: localhost:6379
```

### Option 2: Local Development

```bash
# 1. Clone and navigate
git clone https://github.com/kl3inIT/coursehub-backend.git
cd coursehub-backend

# 2. Start MySQL and Redis
docker run -d --name mysql-coursehub -p 3307:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=CourseHub \
  mysql:8.0

docker run -d --name redis-coursehub -p 6379:6379 redis:latest

# 3. Build and run the application
mvn clean install
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## ⚙️ Configuration

### Environment Profiles

The application supports multiple profiles:

- **`dev`** - Development environment
- **`prod`** - Production environment

### Key Configuration Files

```
src/main/resources/
├── application.yml           # Base configuration
├── application-dev.yml       # Development settings
└── application-prod.yml      # Production settings
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile | `dev` |
| `GOOGLE_CLIENT_ID` | Google OAuth client ID | - |
| `GOOGLE_CLIENT_SECRET` | Google OAuth client secret | - |
| `AWS_ACCESS_KEY` | AWS access key | - |
| `AWS_SECRET_KEY` | AWS secret key | - |
| `SEPAY_WEBHOOK_APIKEY` | SePay webhook API key | - |
| `JWT_SECRET` | JWT secret key | `WaVbZbGzZMYGHUjNYrh87xGyib8ivGWndlsf4bsX2mB47MbdsUTwf2Dsv1TPuBG+` |

### Database Configuration

| Environment | MySQL Port | Database | Username | Password |
|-------------|------------|----------|----------|----------|
| Development | `3307` | `CourseHub` | `root` | `123456` |
| Production | `3306` | `CourseHub` | `root` | `123456` |

### Required .env File

Create a `.env` file in the project root with the following variables:

```env
# Google OAuth
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# AWS S3
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key

# Payment Gateway
SEPAY_WEBHOOK_APIKEY=your_sepay_webhook_key
```

### Service URLs

| Environment | API URL | Frontend URL |
|-------------|---------|--------------|
| Development | `http://localhost:8080` | `http://localhost:3000` |
| Production | `https://api.coursehub.io.vn` | `https://it4beginner.vercel.app` |


## 📁 Project Structure

```
src/main/java/com/coursehub/
├── 📁 components/          # Utility components
├── 📁 config/             # Configuration classes
├── 📁 constant/           # Application constants
├── 📁 controller/         # REST controllers
├── 📁 converter/          # DTO converters
├── 📁 dto/                # Data Transfer Objects
│   ├── 📁 request/        # Request DTOs
│   └── 📁 response/       # Response DTOs
├── 📁 entity/             # JPA entities
├── 📁 enums/              # Enumeration classes
├── 📁 exceptions/         # Custom exceptions
├── 📁 repository/         # Data repositories
├── 📁 service/            # Business logic
│   └── 📁 impl/           # Service implementations
└── 📁 utils/              # Utility classes
```

## 🔧 Development

### Code Style

This project follows standard Java conventions:
- Use **camelCase** for variables and methods
- Use **PascalCase** for classes
- Follow **Spring Boot** best practices

## 🚀 Deployment

### Development Deployment

```bash
# Using override file for development
docker-compose -f docker-compose.yml -f docker-compose.override.yml up -d --build
```

### Production Deployment

```bash
# 1. Set environment variables in .env file
# 2. Deploy using production compose file
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# The production setup includes:
# - Application: kl3init/coursehub:latest
# - Nginx proxy with SSL termination
# - Let's Encrypt SSL certificates
# - Domain: api.coursehub.io.vn
```

### SSL Certificate Setup (Production)

```bash
# Initial certificate generation
docker-compose exec certbot certbot certonly --webroot -w /var/www/certbot -d api.coursehub.io.vn

# Certificate auto-renewal is handled by the certbot container
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Support

For support and questions:
- 📖 Documentation: [Project Documentation](https://docs.google.com/document/d/1yS2gNY6gmu4iALd1iCFMwpMh6yIt_NF82NZSsYMsZ4w/edit?tab=t.0)
- 🐛 Issues: [GitHub Issues](https://github.com/kl3inIT/coursehub-backend/issues)

---
