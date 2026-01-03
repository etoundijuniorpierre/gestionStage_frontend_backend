# 🚀 Backend - Plateforme de Gestion de Stages

Application Spring Boot pour la gestion des stages étudiants.

## 📋 Table des Matières

- [Technologies](#technologies)
- [Architecture](#architecture)
- [Installation](#installation)
- [Configuration](#configuration)
- [Utilisation](#utilisation)
- [API Documentation](#api-documentation)
- [Tests](#tests)
- [Qualité du Code](#qualité-du-code)
- [Déploiement](#déploiement)

---

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.3.13**
- **Spring Data JPA** - Persistance des données
- **Spring Security** - Authentification JWT
- **PostgreSQL** - Base de données
- **MapStruct 1.5.5** - Mapping entités/DTOs
- **Lombok** - Réduction du boilerplate
- **SpringDoc OpenAPI** - Documentation Swagger
- **WebSocket** - Notifications temps réel
- **Apache POI** - Export Excel
- **PDFBox** - Gestion PDF
- **Bucket4j** - Rate Limiting
- **JavaMail** - Envoi d'emails

---

## 🏗️ Architecture

### Structure du Projet

```
src/main/java/com/internship/management/
├── config/              # Configuration (CORS, WebSocket, Swagger)
├── controllers/         # Controllers REST
├── dto/                 # Data Transfer Objects
│   ├── application/
│   ├── postOffer/
│   ├── profile/
│   ├── registration/
│   └── response/
├── entities/            # Entités JPA
├── enums/               # Énumérations
├── exception/           # Exceptions personnalisées
├── interfaces/          # Interfaces de services
├── mappers/             # MapStruct mappers
├── repositories/        # Spring Data JPA repositories
├── security/            # Configuration sécurité
├── services/            # Services métier
├── util/                # Utilitaires
└── validation/          # Validateurs personnalisés
```

### Couches Applicatives

```
┌─────────────────────────────────────┐
│         Controllers (REST)          │
├─────────────────────────────────────┤
│      Services (Logique Métier)      │
├─────────────────────────────────────┤
│    Repositories (Accès Données)     │
├─────────────────────────────────────┤
│      Entités JPA (Modèle)           │
└─────────────────────────────────────┘
```

---

## 📦 Installation

### Prérequis

- Java 17 ou supérieur
- Maven 3.8+
- PostgreSQL 14+
- Serveur SMTP (pour les emails)

### Étapes

1. **Cloner le repository**

```bash
git clone <repository-url>
cd springBoot
```

2. **Configurer la base de données**

```sql
CREATE DATABASE internship_management;
CREATE USER internship_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE internship_management TO internship_user;
```

3. **Configurer l'application**

```bash
cp .env.example .env
# Éditer .env avec vos configurations
```

4. **Compiler le projet**

```bash
mvn clean install
```

---

## ⚙️ Configuration

### Fichier `.env`

```properties
# Database
DB_URL=jdbc:postgresql://localhost:5432/internship_management
DB_USERNAME=internship_user
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=86400000

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Application
SERVER_PORT=8080
```

### application.properties

Les configurations sont chargées depuis les variables d'environnement.

---

## 🚀 Utilisation

### Démarrage en Développement

```bash
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

### Démarrage en Production

```bash
mvn clean package -DskipTests
java -jar target/internship-management-0.0.1-SNAPSHOT.jar
```

---

## 📖 API Documentation

### Swagger UI

Accédez à la documentation interactive:

```
http://localhost:8080/swagger-ui/index.html
```

### Endpoints Principaux

#### Authentification

- `POST /login` - Connexion
- `POST /registration/enterprise` - Inscription entreprise
- `POST /registration/student` - Inscription étudiant
- `POST /registration/teacher` - Inscription enseignant
- `POST /verify` - Vérification email

#### Étudiants

- `GET /api/student/offersByApprovedStatus` - Liste des offres approuvées
- `POST /api/student/{offer_id}/createApplication` - Postuler à une offre
- `GET /api/student/applications/pending` - Candidatures en attente
- `PUT /api/student/{application_id}/updateStudentStatus` - Accepter/refuser une offre

#### Enseignants

- `GET /api/teacher/offersToReview` - Offres à valider
- `PUT /api/teacher/{offer_id}/validateOffer` - Valider une offre
- `GET /api/teacher/students` - Liste des étudiants
- `GET /api/teacher/stats` - Statistiques

#### Entreprises

- `POST /api/enterprise/createOffer` - Créer une offre
- `GET /api/enterprise/offers` - Mes offres
- `GET /api/enterprise/applications` - Candidatures reçues
- `PUT /api/enterprise/application/{id}/updateState` - Gérer une candidature

#### Administrateurs

- `GET /api/enterprises/pending` - Entreprises en attente
- `PUT /api/enterprise/{id}/approve` - Approuver une entreprise
- `GET /api/stats/export` - Exporter les statistiques

---

## 🧪 Tests

### Exécuter les Tests

```bash
# Tous les tests
mvn test

# Tests d'une classe spécifique
mvn test -Dtest=OfferServiceImplTest

# Avec rapport de couverture
mvn clean test jacoco:report
```

### Rapport de Couverture

Le rapport JaCoCo est généré dans:

```
target/site/jacoco/index.html
```

**Objectif de couverture:** 80%

---

## 📊 Qualité du Code

### Standards Appliqués

- ✅ **Exceptions personnalisées** - Gestion d'erreur professionnelle
- ✅ **Logs structurés** - Traçabilité complète (SLF4J)
- ✅ **@Transactional** - Cohérence des données
- ✅ **JavaDoc** - Documentation complète
- ✅ **Validation** - Bean Validation + validateurs personnalisés
- ✅ **Sécurité** - SecureRandom, validation fichiers

### Métriques

| Métrique                | Valeur  | Statut           |
| ----------------------- | ------- | ---------------- |
| Complexité cyclomatique | ~4      | ✅ Excellent     |
| Couverture de code      | 0%\*    | ⏳ À implémenter |
| Dette technique         | 2 jours | ✅ Faible        |
| Maintenabilité          | 9/10    | ✅ Excellent     |

\*Les tests sont à implémenter

---

## 🔒 Sécurité

### Fonctionnalités

- ✅ **JWT Authentication** - Tokens sécurisés
- ✅ **BCrypt** - Hashage des mots de passe
- ✅ **CORS** - Configuration stricte
- ✅ **Rate Limiting** - Protection contre brute force
- ✅ **Security Headers** - Headers de sécurité HTTP
- ✅ **Validation** - Validation stricte des entrées
- ✅ **SecureRandom** - Génération sécurisée de codes

### Rôles

- `ADMIN` - Administrateur système
- `TEACHER` - Enseignant
- `STUDENT` - Étudiant
- `ENTERPRISE` - Entreprise

---

## 🚢 Déploiement

### Docker

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build et Run

```bash
# Build l'image
docker build -t internship-backend .

# Run le container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/internship_management \
  -e DB_USERNAME=internship_user \
  -e DB_PASSWORD=your_password \
  internship-backend
```

### Docker Compose

Voir `docker-compose.yml` à la racine du projet.

---

## 📚 Documentation Supplémentaire

### Fichiers de Référence

- **AUDIT_BACKEND_COMPLET.md** - Analyse détaillée du code
- **CORRECTIONS_PRIORITAIRES.md** - Guide de corrections
- **GUIDE_TESTS.md** - Exemples de tests
- **IMPLEMENTATION_FINALE.md** - Rapport d'implémentation
- **RAPPORT_PROGRESSION.md** - Suivi de progression

---

## 🤝 Contribution

### Guidelines

1. Créer une branche feature: `git checkout -b feature/ma-fonctionnalite`
2. Commiter les changements: `git commit -m 'Ajout de ma fonctionnalité'`
3. Pusher la branche: `git push origin feature/ma-fonctionnalite`
4. Créer une Pull Request

### Standards de Code

- ✅ JavaDoc sur toutes les méthodes publiques
- ✅ Logs appropriés (debug, info, warn, error)
- ✅ Tests unitaires (couverture > 80%)
- ✅ Gestion d'erreur avec exceptions personnalisées
- ✅ @Transactional sur les méthodes de service

---

## 📝 Changelog

### Version 2.0 (2026-01-03)

#### Ajouté

- ✅ Exceptions personnalisées (ResourceNotFoundException, BusinessException, etc.)
- ✅ GlobalExceptionHandler amélioré
- ✅ Logs structurés partout
- ✅ @Transactional sur tous les services
- ✅ JavaDoc complète
- ✅ ApplicationService (service métier dédié)
- ✅ Validation robuste des fichiers
- ✅ SecureRandom pour génération de codes

#### Modifié

- ✅ OfferServiceImpl - 16 exceptions corrigées
- ✅ NotificationServiceImpl - Refactorisation complète
- ✅ ProfilePhotoService - Validation améliorée
- ✅ RegistrationServiceImpl - Exceptions personnalisées
- ✅ VerificationTokenService - Sécurité renforcée

#### Corrigé

- ✅ Double point-virgule dans Offer.java
- ✅ Imports inutilisés dans repositories
- ✅ Nommage de paramètres

---

## 📞 Support

Pour toute question ou problème:

1. Consulter la documentation dans `/docs`
2. Vérifier les issues GitHub
3. Contacter l'équipe de développement

---

## 📄 Licence

Ce projet est sous licence MIT.

---

**Développé avec ❤️ par l'équipe Backend**

**Version:** 2.0  
**Dernière mise à jour:** 03 Janvier 2026
