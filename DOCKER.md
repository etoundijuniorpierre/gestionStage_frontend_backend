# 🐳 Docker Deployment Guide

## 📋 Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- 4GB RAM minimum
- 10GB free disk space

## 🚀 Quick Start

### 1. Clone and Navigate

```bash
cd gestionStage_frontend_backend
```

### 2. Configure Environment

```bash
# Copy example environment file
cp .env.example .env

# Edit .env with your values
nano .env  # or use your preferred editor
```

**⚠️ IMPORTANT**: Update these values in `.env`:

- `DB_PASSWORD` - Strong database password
- `JWT_SECRET` - Generate with: `openssl rand -hex 32`
- `MAIL_USERNAME` - Your email
- `MAIL_PASSWORD` - App-specific password

### 3. Start All Services

```bash
docker-compose up -d
```

This will start:

- **PostgreSQL** on port 5432
- **Backend API** on port 8080
- **Frontend** on port 80

### 4. Verify Deployment

```bash
# Check all services are running
docker-compose ps

# Check logs
docker-compose logs -f

# Check health
docker-compose ps
```

### 5. Access Application

- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html

---

## 🛠️ Docker Commands

### Start Services

```bash
# Start all services
docker-compose up -d

# Start specific service
docker-compose up -d backend

# View logs
docker-compose logs -f
docker-compose logs -f backend
```

### Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v
```

### Rebuild Services

```bash
# Rebuild all
docker-compose build

# Rebuild specific service
docker-compose build backend

# Rebuild and restart
docker-compose up -d --build
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres

# Last 100 lines
docker-compose logs --tail=100 backend
```

### Execute Commands

```bash
# Backend shell
docker-compose exec backend sh

# PostgreSQL shell
docker-compose exec postgres psql -U postgres -d internship

# View backend logs directory
docker-compose exec backend ls -la /app/logs
```

---

## 📊 Service Details

### PostgreSQL

- **Image**: postgres:15-alpine
- **Port**: 5432
- **Volume**: `internship-postgres-data`
- **Health Check**: Every 10s

### Backend (Spring Boot)

- **Build**: Multi-stage (Maven + JRE)
- **Port**: 8080
- **Volumes**:
  - `backend_logs` - Application logs
- **Health Check**: Every 30s
- **User**: Non-root (spring:spring)

### Frontend (React + Nginx)

- **Build**: Multi-stage (Node + Nginx)
- **Port**: 80
- **Features**:
  - Gzip compression
  - Static asset caching
  - Security headers
  - SPA routing

---

## 🔧 Configuration

### Environment Variables

All configuration is done via `.env` file. See `.env.example` for all available options.

**Database**:

```env
DB_NAME=internship
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

**JWT**:

```env
JWT_SECRET=your_secret_key
```

**Email**:

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

**CORS**:

```env
CORS_ALLOWED_ORIGINS=http://localhost,http://yourdomain.com
```

### Ports

Default ports can be changed in `docker-compose.yml`:

```yaml
ports:
  - "8080:8080" # Change left side: "9000:8080"
```

---

## 📦 Volumes

### Persistent Data

- **postgres_data**: Database files
- **backend_logs**: Application logs

### Backup Database

```bash
# Create backup
docker-compose exec postgres pg_dump -U postgres internship > backup.sql

# Restore backup
docker-compose exec -T postgres psql -U postgres internship < backup.sql
```

### View Logs

```bash
# Backend logs location
docker volume inspect internship-backend-logs

# Access logs
docker run --rm -v internship-backend-logs:/logs alpine ls -la /logs
```

---

## 🌐 Networks

All services communicate via `internship-network` bridge network.

```bash
# Inspect network
docker network inspect internship-network

# View connected containers
docker network inspect internship-network | grep Name
```

---

## 🔍 Troubleshooting

### Services Won't Start

```bash
# Check logs
docker-compose logs

# Check specific service
docker-compose logs backend

# Restart services
docker-compose restart
```

### Database Connection Issues

```bash
# Check PostgreSQL is healthy
docker-compose ps postgres

# Test connection
docker-compose exec postgres psql -U postgres -d internship -c "SELECT 1"

# Check backend can reach database
docker-compose exec backend ping postgres
```

### Backend Not Starting

```bash
# Check environment variables
docker-compose config

# View backend logs
docker-compose logs backend

# Check health
docker-compose exec backend wget -O- http://localhost:8080/actuator/health
```

### Frontend Not Loading

```bash
# Check nginx config
docker-compose exec frontend cat /etc/nginx/conf.d/default.conf

# Check nginx logs
docker-compose exec frontend cat /var/log/nginx/error.log

# Test nginx
docker-compose exec frontend nginx -t
```

### Port Already in Use

```bash
# Find process using port
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080

# Change port in docker-compose.yml
ports:
  - "9090:8080"  # Use 9090 instead
```

---

## 🚀 Production Deployment

### Security Checklist

- [ ] Change all default passwords
- [ ] Use strong JWT secret (256+ bits)
- [ ] Enable HTTPS (add reverse proxy)
- [ ] Restrict CORS origins
- [ ] Set `LOG_LEVEL=WARN` or `ERROR`
- [ ] Regular backups
- [ ] Monitor logs
- [ ] Update images regularly

### Recommended Setup

```yaml
# Use specific versions (not latest)
postgres:
  image: postgres:15.3-alpine

# Add restart policy
restart: always

# Limit resources
deploy:
  resources:
    limits:
      cpus: "2"
      memory: 2G
```

### HTTPS with Nginx Reverse Proxy

```yaml
# Add to docker-compose.yml
nginx-proxy:
  image: nginx:alpine
  ports:
    - "443:443"
  volumes:
    - ./nginx-proxy.conf:/etc/nginx/nginx.conf
    - ./ssl:/etc/nginx/ssl
```

---

## 📝 Maintenance

### Update Services

```bash
# Pull latest images
docker-compose pull

# Rebuild and restart
docker-compose up -d --build
```

### Clean Up

```bash
# Remove stopped containers
docker-compose rm

# Remove unused images
docker image prune -a

# Remove unused volumes
docker volume prune
```

### Monitor Resources

```bash
# View resource usage
docker stats

# View specific service
docker stats internship-backend
```

---

## 🆘 Support

### Logs Location

- **Backend**: `docker volume inspect internship-backend-logs`
- **Frontend**: `docker-compose logs frontend`
- **Database**: `docker-compose logs postgres`

### Health Checks

```bash
# Backend
curl http://localhost:8080/actuator/health

# Frontend
curl http://localhost/

# Database
docker-compose exec postgres pg_isready
```

---

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Nginx Docker Guide](https://hub.docker.com/_/nginx)

---

**Version**: 1.0.0  
**Last Updated**: 2026-01-02
