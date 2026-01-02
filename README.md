# 🎓 Internship Management System

> Modern, secure, and scalable full-stack application for managing internships

[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.3-green)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/Frontend-React%2019-blue)](https://reactjs.org/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL%2015-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)

---

## ✨ Features

### 👥 Multi-Role System

- **Students**: Browse offers, apply, track applications
- **Companies**: Post offers, manage applications
- **Teachers**: Validate offers, monitor students
- **Administrators**: Manage all entities, export data

### 🔐 Security

- JWT authentication
- Rate limiting (100 req/min)
- CORS configuration
- Security headers (HSTS, CSP, etc.)
- File validation (type, size, signature)

### 🎨 Modern UI

- Dark/Light mode
- Glassmorphism design
- Micro-animations
- Toast notifications
- Responsive design

### 📊 Features

- Real-time notifications (WebSocket)
- PDF generation
- Excel export
- Email notifications
- File upload (CV, cover letters)

---

## 🚀 Quick Start with Docker

### Prerequisites

- Docker & Docker Compose
- 4GB RAM minimum

### 1. Configure Environment

```bash
cp .env.example .env
# Edit .env with your credentials
```

### 2. Start Application

```bash
docker-compose up -d
```

### 3. Access

- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html

📖 See [DOCKER.md](DOCKER.md) for detailed Docker documentation.

---

## 💻 Local Development

### Backend (Spring Boot)

#### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL 15+

#### Setup

```bash
cd springBoot

# Configure environment
cp .env.example .env
# Edit .env with your values

# Run database
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=internship \
  -e POSTGRES_PASSWORD=yourpassword \
  postgres:15-alpine

# Start application
mvn spring-boot:run
```

📖 See [springBoot/README.md](springBoot/README.md) for backend documentation.

### Frontend (React)

#### Prerequisites

- Node.js 18+
- pnpm or npm

#### Setup

```bash
cd react

# Install dependencies
npm install

# Start development server
npm run dev
```

📖 See [react/README.md](react/README.md) for frontend documentation.

---

## 📁 Project Structure

```
gestionStage_frontend_backend/
├── springBoot/              # Backend (Spring Boot)
│   ├── src/
│   │   ├── main/java/com/internship/management/
│   │   │   ├── config/      # Configuration (CORS, Security, etc.)
│   │   │   ├── controllers/ # REST Controllers
│   │   │   ├── dto/         # Data Transfer Objects
│   │   │   ├── entities/    # JPA Entities
│   │   │   ├── exception/   # Exception Handling
│   │   │   ├── repositories/# JPA Repositories
│   │   │   ├── security/    # Security (JWT, Filters)
│   │   │   ├── services/    # Business Logic
│   │   │   ├── util/        # Utilities
│   │   │   └── validation/  # Custom Validators
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml
│   ├── .env.example
│   ├── Dockerfile
│   ├── pom.xml
│   └── SECURITY.md
│
├── react/                   # Frontend (React + TypeScript)
│   ├── src/
│   │   ├── api/            # API Services
│   │   ├── components/     # React Components
│   │   │   ├── ui/        # Reusable UI Components
│   │   │   ├── admin/     # Admin Components
│   │   │   ├── teacher/   # Teacher Components
│   │   │   ├── entreprise/# Company Components
│   │   │   └── etudiant/  # Student Components
│   │   ├── store/         # State Management (Zustand)
│   │   ├── types/         # TypeScript Types
│   │   ├── App.tsx
│   │   └── index.css      # Design System
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── docker-compose.yml      # Docker Orchestration
├── .env.example           # Environment Template
├── DOCKER.md              # Docker Documentation
└── README.md              # This file
```

---

## 🛡️ Security Features

### Backend

- ✅ Externalized credentials (environment variables)
- ✅ Rate limiting (Bucket4j)
- ✅ CORS configuration
- ✅ Security headers (HSTS, CSP, X-Frame-Options, etc.)
- ✅ File validation (MIME type, signature, size)
- ✅ JWT authentication
- ✅ BCrypt password hashing

### Frontend

- ✅ Protected routes
- ✅ Token-based authentication
- ✅ Input validation
- ✅ XSS protection

---

## 📊 API Documentation

### Swagger UI

Access interactive API documentation at:

```
http://localhost:8080/swagger-ui.html
```

### Main Endpoints

#### Authentication

- `POST /login` - User login
- `POST /registration/registerStudent` - Student registration
- `POST /registration/registerTeacher` - Teacher registration
- `POST /registration/registerEnterprise` - Company registration

#### Students

- `GET /api/student/pendingApplicationsOfStudent` - Get applications
- `POST /api/student/{offer_id}/createApplication` - Apply to offer

#### Companies

- `POST /api/enterprise/createOffer` - Create offer
- `GET /api/enterprise/listOfOffers` - List offers
- `PUT /api/enterprise/application/{id}/validate` - Validate application

#### Teachers

- `PUT /api/teacher/offers/{id}/validate` - Validate offer
- `GET /api/teacher/offersApprovedByTeacher` - List approved offers

#### Admin

- `PUT /api/admin/Enterprise/{id}/approve` - Approve company
- `GET /api/admin/internships.xlsx` - Export internships

---

## 🧪 Testing

### Backend

```bash
cd springBoot
mvn test
```

### Frontend

```bash
cd react
npm run test
```

---

## 📈 Performance

### Backend

- Multi-stage Docker build (optimized image size)
- Connection pooling (HikariCP)
- Structured logging with rotation
- Health checks

### Frontend

- Code splitting
- Lazy loading
- Gzip compression
- Static asset caching (1 year)
- Optimized bundle size

---

## 🔧 Configuration

### Environment Variables

All sensitive configuration is externalized via environment variables.

**Required**:

- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing key (256+ bits)
- `MAIL_USERNAME` - Email username
- `MAIL_PASSWORD` - Email password

**Optional**:

- `LOG_LEVEL` - Logging level (default: INFO)
- `CORS_ALLOWED_ORIGINS` - Allowed origins (default: localhost)
- `MAX_FILE_SIZE` - Max upload size (default: 50MB)

See `.env.example` for all available options.

---

## 📝 Logging

### Backend Logs

- **Location**: `logs/` directory
- **Files**:
  - `internship-management.log` - General logs
  - `internship-management-error.log` - Errors only
  - `internship-management-security.log` - Security events
- **Rotation**: Daily, 10MB max, 30-90 days retention

### Docker Logs

```bash
docker-compose logs -f backend
docker-compose logs -f frontend
```

---

## 🚀 Deployment

### Production Checklist

- [ ] Change all default passwords
- [ ] Generate strong JWT secret
- [ ] Configure CORS for production domain
- [ ] Set `LOG_LEVEL=WARN` or `ERROR`
- [ ] Enable HTTPS
- [ ] Set up database backups
- [ ] Configure monitoring
- [ ] Update Docker images regularly

### Recommended Stack

- **Hosting**: AWS, Azure, GCP, or DigitalOcean
- **Reverse Proxy**: Nginx with SSL/TLS
- **Database**: Managed PostgreSQL
- **Monitoring**: Prometheus + Grafana
- **Logs**: ELK Stack or CloudWatch

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Authors

- **Development Team** - Initial work

---

## 🙏 Acknowledgments

- Spring Boot team
- React team
- All contributors

---

## 📞 Support

For support, email support@internship-management.com or open an issue.

---

**Version**: 1.0.0  
**Status**: Production Ready  
**Last Updated**: 2026-01-02
