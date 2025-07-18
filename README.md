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
- [API Documentation](#-api-documentation)
- [Development](#-development)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)

## 🌟 Overview

CourseHub Backend is a robust Spring Boot application that provides RESTful APIs for an online learning platform. It handles course management, user authentication, payment processing, analytics, and real-time communications through WebSocket.

**Key Capabilities:**
- Multi-role user management (Guest, Learner, Manager, Admin)
- Complete course lifecycle management
- Secure payment processing with invoice generation
- Real-time notifications and announcements
- Comprehensive analytics and reporting
- Content moderation and reporting system

## ✨ Features

### 🔐 Authentication & Security
- **JWT Authentication** - Secure token-based authentication
- **Google OAuth2 Integration** - Social login support
- **Role-based Authorization** - Multi-level access control (Guest/Learner/Manager/Admin)
- **Password Reset** - OTP-based password recovery

### 👥 User Management
- **User Registration** - Email verification with OTP
- **Profile Management** - Avatar upload, personal information
- **Manager Creation** - Admin can create manager accounts
- **User Status Control** - Ban/unban users, warning system

### 📚 Course Management
- **Course CRUD** - Create, read, update, delete courses
- **Module & Lesson Structure** - Hierarchical content organization
- **Video Content** - AWS S3 integration for video storage
- **Course Reviews** - Student feedback and rating system
- **Progress Tracking** - Lesson completion and watch time analytics

### 💳 Payment Processing
- **SePay Integration** - Vietnamese payment gateway
- **TPBank Support** - Bank transfer processing
- **Invoice Generation** - PDF invoice creation and email delivery
- **Discount System** - Coupon codes with expiry management
- **Payment History** - Complete transaction records with Excel export

### 📊 Analytics & Reporting
- **Dashboard Analytics** - Revenue, enrollment, course performance
- **Export Capabilities** - Excel/PDF report generation
- **Manager Analytics** - Course-specific performance metrics
- **Monthly Insights** - Growth trends and statistical analysis

### 🔔 Real-time Communication
- **WebSocket Service** - Live notifications and announcements
- **Comment System** - Real-time course discussions
- **Notification System** - User activity notifications
- **Announcement Broadcasting** - Targeted messaging by user role

### 🛡️ Content Moderation
- **Report System** - User-generated content reporting
- **Content Hiding** - Automatic content moderation
- **Warning System** - Progressive user discipline
- **Feedback Management** - Admin response to user feedback

## 🛠 Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.4.5 |
| **Security** | Spring Security + JWT | 6.x |
| **Database** | MySQL | 8.0 |
| **Cache** | Redis | Latest |
| **Build Tool** | Maven | 3.9.5+ |
| **Cloud Storage** | AWS S3 | ap-southeast-2 |
| **Email Service** | Gmail SMTP | - |
| **Payment Gateway** | SePay + TPBank | - |
| **Real-time** | WebSocket (STOMP) | - |
| **PDF Generation** | iText | 7.x |
| **Excel Export** | Apache POI | 5.x |
| **Containerization** | Docker + Docker Compose | - |

## 📋 Prerequisites

Before running this application, make sure you have:

- ☕ **Java 21** or higher
- 🛠️ **Maven 3.9.5** or higher
- 🗄️ **MySQL 8.0** or higher
- 🔄 **Redis** (for caching and sessions)
- 🐳 **Docker** & **Docker Compose** (recommended)
- 🔑 **Google OAuth Credentials** (for social login)
- 💳 **SePay Account** (for payment processing)
- ☁️ **AWS S3 Bucket** (for file storage)

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# 1. Clone the repository
git clone <repository-url>
cd coursehub-backend

# 2. Create environment file
cp .env.example .env
# Edit .env with your configuration (see Configuration section)

# 3. Start all services
docker-compose up -d --build

# 4. Verify services are running
docker ps

# 5. Check application health
curl http://localhost:8080/actuator/health

# 6. Access the application
# API: http://localhost:8080
# MySQL: localhost:3307 (root/123456)
# Redis: localhost:6379
```

### Option 2: Local Development

```bash
# 1. Clone and navigate
git clone <repository-url>
cd coursehub-backend

# 2. Start MySQL and Redis
docker run -d --name mysql-coursehub -p 3307:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=CourseHub \
  mysql:8.0

docker run -d --name redis-coursehub -p 6379:6379 redis:latest

# 3. Set environment variables
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret
export AWS_ACCESS_KEY=your_aws_access_key
export AWS_SECRET_KEY=your_aws_secret_key
export SEPAY_WEBHOOK_APIKEY=your_sepay_key

# 4. Build and run
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## ⚙️ Configuration

### Environment Profiles

- **`dev`** - Development environment (localhost:8080)
- **`prod`** - Production environment (api.coursehub.io.vn)

### Required Environment Variables

Create a `.env` file in the project root:

```env
# Google OAuth2
GOOGLE_CLIENT_ID=your_google_oauth_client_id
GOOGLE_CLIENT_SECRET=your_google_oauth_client_secret

# AWS S3 Configuration
AWS_ACCESS_KEY=your_aws_access_key_id
AWS_SECRET_KEY=your_aws_secret_access_key

# Payment Gateway
SEPAY_WEBHOOK_APIKEY=your_sepay_webhook_api_key
```

### Application Configuration

| Configuration | Development | Production |
|---------------|-------------|------------|
| **Database** | MySQL on port 3307 | MySQL on port 3306 |
| **Redis** | localhost:6379 | redis:6379 |
| **JWT Expiration** | 30 days | 1 day |
| **Frontend URL** | localhost:3000 | it4beginner.vercel.app |
| **API URL** | localhost:8080 | api.coursehub.io.vn |

### Bank Transfer Configuration

```yaml
bank:
  bank-number: 22226376000
  bank-code: TPBank
  account-holder: NHU DINH NHAT
```

### Email Service Configuration

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: it4beginer@gmail.com
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## 📁 Project Structure

```
coursehub-backend/
├── 📁 src/main/java/com/coursehub/
│   ├── 📁 components/          # Utility components
│   │   ├── DiscountScheduler.java
│   │   ├── OtpUtil.java
│   │   └── CustomJwtDecoder.java
│   ├── 📁 config/             # Configuration classes
│   │   ├── WebSocketConfig.java
│   │   ├── SecurityConfig.java
│   │   └── AwsConfig.java
│   ├── 📁 controller/         # REST API controllers
│   │   ├── AuthenticationController.java
│   │   ├── CourseController.java
│   │   ├── PaymentController.java
│   │   └── [17+ controllers]
│   ├── 📁 dto/                # Data Transfer Objects
│   │   ├── 📁 request/        # API request DTOs
│   │   └── 📁 response/       # API response DTOs
│   ├── 📁 entity/             # JPA database entities
│   │   ├── UserEntity.java
│   │   ├── CourseEntity.java
│   │   ├── PaymentEntity.java
│   │   └── [20+ entities]
│   ├── 📁 service/            # Business logic services
│   │   ├── AuthenticationService.java
│   │   ├── CourseService.java
│   │   ├── PaymentService.java
│   │   └── 📁 impl/           # Service implementations
│   ├── 📁 repository/         # Data access layer
│   ├── 📁 exceptions/         # Custom exception classes
│   ├── 📁 enums/              # Enumeration types
│   └── 📁 utils/              # Utility classes
├── 📁 src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── 📁 templates/          # Email templates
├── 📁 docker-compose files
├── 📁 target/                 # Build output
└── pom.xml                    # Maven configuration
```

## 📚 API Documentation

### Core API Endpoints

| Category | Endpoint | Description |
|----------|----------|-------------|
| **Auth** | `POST /api/auth/login` | User authentication |
| **Auth** | `POST /api/auth/register` | User registration |
| **Auth** | `POST /api/auth/google` | Google OAuth login |
| **Courses** | `GET /api/courses` | Get course catalog |
| **Courses** | `POST /api/courses` | Create new course |
| **Payment** | `POST /api/payments` | Process payment |
| **Analytics** | `GET /api/analytics/dashboard` | Get dashboard data |
| **Users** | `GET /api/users/profile` | Get user profile |
| **Admin** | `GET /api/admin/users` | User management |

### Authentication

All protected endpoints require JWT token in header:
```
Authorization: Bearer <jwt_token>
```

### Response Format

All API responses follow this structure:
```json
{
  "message": "Success",
  "data": { ... },
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

## 🔧 Development

### Running Tests

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### Code Style

- Follow **Java Code Conventions**
- Use **camelCase** for variables and methods
- Use **PascalCase** for classes
- Use **UPPER_SNAKE_CASE** for constants
- Follow **Spring Boot** best practices

### Database Management

```bash
# Reset database
docker-compose down -v
docker-compose up -d mysql

# Access MySQL console
docker exec -it mysql-coursehub mysql -u root -p123456 CourseHub
```

### Scheduled Tasks

- **Discount Status Update**: Every 5 minutes
- **Announcement Processing**: Every 30 seconds
- **Session Cleanup**: Daily at midnight

## 🚀 Deployment

### Development Deployment

```bash
docker-compose -f docker-compose.yml -f docker-compose.override.yml up -d --build
```

### Production Deployment

```bash
# 1. Set production environment variables
export SPRING_PROFILES_ACTIVE=prod

# 2. Deploy with production configuration
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 3. Monitor logs
docker-compose logs -f coursehub-backend
```

### Health Checks

```bash
# Application health
curl https://api.coursehub.io.vn/actuator/health

# Database connectivity
curl https://api.coursehub.io.vn/actuator/health/db
```

## 🔍 Monitoring & Troubleshooting

### Application Metrics

Spring Actuator endpoints are available at:
- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check MySQL container
   docker logs mysql-coursehub
   ```

2. **Redis Connection Failed**
   ```bash
   # Check Redis container
   docker logs redis-coursehub
   ```

3. **JWT Token Issues**
   ```bash
   # Verify JWT secret configuration
   echo $JWT_SECRET
   ```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Write unit tests for new features
- Update documentation for API changes
- Follow existing code style
- Test with both dev and prod profiles

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Support

For support and questions:
- 📖 **Documentation**: [Project Documentation](https://docs.google.com/document/d/1yS2gNY6gmu4iALd1iCFMwpMh6yIt_NF82NZSsYMsZ4w/edit?tab=t.0)
- 🐛 **Issues**: Create an issue in the repository
- 💬 **Discussions**: Use repository discussions for questions

---

**Built with ❤️ for the CourseHub learning platform**
