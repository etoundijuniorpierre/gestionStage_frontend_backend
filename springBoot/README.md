# 🚀 Internship Management API - Backend Spring Boot

API REST pour la gestion de stages avec Spring Boot 3.3.13 et Java 17, déployée sur Render avec base de données Neon PostgreSQL.

## 📋 Vue d'ensemble

### Stack technique
- **Framework** : Spring Boot 3.3.13
- **Java** : Version 17
- **Base de données** : Neon PostgreSQL
- **Authentification** : JWT
- **Email** : Gmail SMTP
- **Documentation** : OpenAPI/Swagger
- **Déploiement** : Render (Docker)

### Fonctionnalités principales
- **Gestion des utilisateurs** (Étudiants, Enseignants, Entreprises, Admin)
- **Authentification JWT** avec rôles
- **Upload de fichiers** (Photos de profil, CV, documents)
- **Gestion des offres** de stage
- **Système de candidatures**
- **Notifications par email**
- **WebSocket** pour les notifications temps réel

## 🚀 Déploiement en Production

### Configuration de production
- **URL API** : `https://internship-management-api.onrender.com/api`
- **Port** : 9080
- **Branche** : `prod`
- **Base de données** : Neon PostgreSQL
- **Health Check** : `/api/health`

### Variables d'environnement (Production)
```bash
# Base de données Neon
DB_URL=jdbc:postgresql://ep-restless-fire-akc5hcb9-pooler.c-3.us-west-2.aws.neon.tech/internship?user=neondb_owner&password=npg_aMk9Hj3QPsyY&sslmode=require&channelBinding=require
DB_USERNAME=neondb_owner
DB_PASSWORD=npg_aMk9Hj3QPsyY

# JWT et sécurité
JWT_SECRET=<generated-secret>

# Configuration email
MAIL_USERNAME=teamkf48inscription@gmail.com
MAIL_PASSWORD=njdd ruhm daiw iwld

# Spring Boot
SPRING_PROFILES_ACTIVE=prod
PORT=9080
JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"
```

### Workflow de déploiement
1. **Développement** sur branche `develop`
2. **Tests** et validation
3. **Merge** vers branche `prod`
4. **Déploiement automatique** sur Render

```bash
# Déployer en production
git checkout prod
git merge develop
git push origin prod
```

## 🛠️ Installation et Démarrage

### Prérequis
- Java 17+
- Maven 3.9+
- PostgreSQL (développement)

### Démarrage local

#### 1. Cloner le projet
```bash
git clone <repository-url>
cd gestionStage_frontend_backend/springBoot
```

#### 2. Configuration locale
```bash
# Copier le template de configuration
cp src/main/resources/application-template.properties src/main/resources/application.properties

# Éditer les variables locales
nano src/main/resources/application.properties
```

#### 3. Démarrer l'application
```bash
./mvnw spring-boot:run
```

L'API démarre sur `http://localhost:9080/api`

### Avec Docker
```bash
# Build
docker build -t internship-api .

# Run
docker run -p 9080:9080 internship-api
```

## 📁 Structure du projet

```
springBoot/
├── src/
│   ├── main/
│   │   ├── java/com/internship/management/
│   │   │   ├── config/          # Configuration sécurité, CORS
│   │   │   ├── controllers/     # REST Controllers
│   │   │   ├── dto/           # Data Transfer Objects
│   │   │   ├── entities/       # Entités JPA
│   │   │   ├── exceptions/     # Gestion des erreurs
│   │   │   ├── repositories/   # Repositories Spring Data
│   │   │   ├── security/       # Configuration JWT
│   │   │   ├── services/       # Logique métier
│   │   │   └── utils/         # Utilitaires
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-template.properties
│   └── test/                 # Tests unitaires et intégration
├── Dockerfile                 # Configuration Docker pour production
├── render.yaml               # Configuration déploiement Render
├── pom.xml                  # Configuration Maven
└── README.md                # Ce fichier
```

## 🔧 Configuration

### application.properties
```properties
# Serveur
server.port=9080
server.servlet.context-path=/api

# Base de données
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=${JWT_SECRET}

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

# CORS
cors.allowed-origins=${CORS_ALLOWED_ORIGINS}
```

### Base de données
- **Production** : Neon PostgreSQL (serverless)
- **Développement** : PostgreSQL local
- **Tests** : H2 (en mémoire)
- **Migration** : Automatique avec Hibernate (`ddl-auto=update`)

## 📚 Documentation API

### Swagger/OpenAPI
- **URL locale** : `http://localhost:9080/api/swagger-ui.html`
- **URL production** : `https://internship-management-api.onrender.com/api/swagger-ui.html`

### Endpoints principaux

#### Authentification
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh-token
POST /api/auth/logout
```

#### Utilisateurs
```
GET    /api/users/profile
PUT    /api/users/profile
POST   /api/users/upload-photo
```

#### Offres de stage
```
GET    /api/offres
POST   /api/offres
GET    /api/offres/{id}
PUT    /api/offres/{id}
DELETE /api/offres/{id}
```

#### Entreprises
```
GET    /api/enterprises
POST   /api/enterprises
GET    /api/enterprises/{id}
PUT    /api/enterprises/{id}
```

## 🧪 Tests

### Lancer les tests
```bash
# Tests unitaires
./mvnw test

# Tests d'intégration
./mvnw verify

# Coverage
./mvnw jacoco:report
```

### Structure des tests
```
src/test/java/
├── unitaires/           # Tests unitaires
├── integration/         # Tests d'intégration
└── e2e/              # Tests end-to-end
```

## 🔐 Sécurité

### JWT Configuration
- **Expiration** : 24h
- **Refresh token** : 7 jours
- **Rôles** : STUDENT, TEACHER, ENTERPRISE, ADMIN
- **Password hashing** : BCrypt

### CORS
- **Origines autorisées** : Configurables via `CORS_ALLOWED_ORIGINS`
- **Méthodes** : GET, POST, PUT, DELETE, OPTIONS
- **Headers** : Authorization, Content-Type

### Validation
- **Input validation** : Annotations Bean Validation
- **DTO validation** : Contraintes personnalisées
- **Error handling** : GlobalExceptionHandler

## 📊 Monitoring et Logs

### Logs
```properties
# Niveaux de log configurés
logging.level.root=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.com.internship=DEBUG
```

### Health Check
```bash
# Endpoint de santé
GET /api/health

# Response
{
  "status": "UP",
  "timestamp": "2024-03-15T10:30:00Z",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

### Production Monitoring
- **Render Dashboard** : Métriques et logs
- **Health checks** : Surveillance automatique
- **Error tracking** : Logs structurés

## 🚨 Dépannage

### Problèmes courants

#### 1. Connexion base de données
```bash
# Vérifier la chaîne de connexion
echo $DB_URL

# Tester avec psql
psql "$DB_URL"
```

#### 2. Port déjà utilisé
```bash
# Tuer le processus sur le port 9080
lsof -ti:9080 | xargs kill -9

# Ou changer le port
server.port=9081
```

#### 3. Problèmes de build
```bash
# Nettoyer et recompiler
./mvnw clean install

# Forcer re-téléchargement des dépendances
./mvnw clean install -U
```

## 🤝 Développement

### Standards de code
- **Java** : Suivre les conventions Spring Boot
- **Packages** : Structure logique par fonctionnalité
- **Comments** : JavaDoc pour les méthodes publiques
- **Tests** : Couverture minimale 80%

### Git workflow
```bash
# Créer une branche feature
git checkout -b feature/nouvelle-fonctionnalite

# Commiter les changements
git add .
git commit -m "feat: ajouter nouvelle fonctionnalité"

# Push et créer PR
git push origin feature/nouvelle-fonctionnalite
```

## 📝 Notes de version

### v1.0.0 (Production)
- ✅ Déploiement sur Render
- ✅ Base de données Neon
- ✅ Authentification JWT
- ✅ Upload de fichiers
- ✅ Notifications email
- ✅ Documentation Swagger

---

**🎯 API prête pour la production !**
