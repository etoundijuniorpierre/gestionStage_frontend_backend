# 🎓 Gestion de Stages - Application Complète

Application de gestion de stages avec frontend React et backend Spring Boot, déployée sur Vercel (frontend) et Render (backend) avec base de données Neon PostgreSQL.

## 📋 Vue d'ensemble

### Architecture
- **Frontend** : React 19 + TypeScript + Vite + Tailwind CSS
- **Backend** : Spring Boot 3.3.13 + Java 17
- **Base de données** : Neon PostgreSQL
- **Déploiement** : Vercel (frontend) + Render (backend)
- **Authentification** : JWT
- **Email** : Gmail SMTP

### Rôles utilisateurs
- **Étudiants** : Consultation des stages et candidatures
- **Enseignants** : Gestion des offres et entreprises
- **Entreprises** : Publication d'offres et gestion des candidatures
- **Admin** : Administration complète de la plateforme

## 🚀 Déploiement en Production

### URLs de production
- **Frontend** : `https://gestion-stage-frontend-backend.vercel.app`
- **Backend API** : `https://gestionstage-frontend-backend.onrender.com/api`

### Workflow de déploiement
1. **Développement** sur branche `develop`
2. **Tests** et validation
3. **Merge** vers branche `prod`
4. **Déploiement automatique** sur Render et Vercel

```bash
# Déployer en production
git checkout prod
git merge develop
git push origin prod
```

## 📁 Structure du projet

```
gestionStage_frontend_backend/
├── react/                    # Frontend React (Vite)
│   ├── src/
│   │   ├── components/       # Composants réutilisables
│   │   ├── pages/          # Pages principales
│   │   ├── hooks/          # Hooks personnalisés
│   │   └── services/       # Services API
│   ├── public/
│   ├── package.json
│   ├── vite.config.ts
│   └── vercel.json
├── springBoot/              # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Code source Java
│   │   │   └── resources/  # Configuration
│   │   └── test/          # Tests
│   ├── Dockerfile
│   ├── render.yaml
│   └── pom.xml
├── docker-compose.yml       # Développement local
└── README.md              # Ce fichier
```

## 🛠️ Installation et Démarrage

### Prérequis
- Node.js 18+
- Java 17+
- Maven 3.9+
- Git

### Démarrage local

#### 1. Cloner le projet
```bash
git clone <repository-url>
cd gestionStage_frontend_backend
```

#### 2. Backend (Spring Boot)
```bash
cd springBoot
./mvnw spring-boot:run
```
L'API démarre sur `http://localhost:9080/api`

#### 3. Frontend (React + Vite)
```bash
cd react
npm install
npm run dev
```
L'application démarre sur `http://localhost:5173`

#### 4. Avec Docker Compose
```bash
docker-compose up -d
```

## 🔧 Configuration

### Variables d'environnement

#### Backend
- `DB_HOST` : Hôte PostgreSQL
- `DB_NAME` : Nom de la base de données
- `DB_USER` : Nom d'utilisateur base de données
- `DB_PASSWORD` : Mot de passe base de données
- `JWT_SECRET` : Clé secrète JWT
- `MAIL_USERNAME` : Email Gmail
- `MAIL_PASSWORD` : Mot de passe Gmail
- `ALLOWED_ORIGINS` : Origines CORS autorisées

#### Frontend
- `VITE_API_URL` : URL de l'API backend

### Base de données
- **Développement** : PostgreSQL local ou Docker
- **Production** : Neon PostgreSQL
- **Migration** : Automatique avec Hibernate (`ddl-auto=update`)

## 📚 Documentation détaillée

- [📖 Documentation Backend](./springBoot/README.md)
- [🎨 Documentation Frontend](./react/README.md)

## 🧪 Tests

### Backend
```bash
cd springBoot
./mvnw test
```

### Frontend
```bash
cd react
npm test
```

## 🔐 Sécurité

- **JWT** : Tokens d'authentification sécurisés
- **CORS** : Origines autorisées configurées
- **SSL/TLS** : Connexions HTTPS obligatoires
- **Password hashing** : BCrypt pour les mots de passe
- **Input validation** : Validation des données utilisateur

## 📊 Monitoring et Logs

### Production
- **Render Dashboard** : Logs et métriques backend
- **Vercel Dashboard** : Logs et performances frontend
- **Health Check** : Surveillance de l'API

### Développement
- **Spring Boot** : Logs DEBUG disponibles
- **React** : Erreurs détaillées dans console

## 🤝 Contributeurs

### Workflow de contribution
1. **Fork** le projet
2. **Créer une branche** feature/nom-feature
3. **Développer** et tester
4. **Push** vers le fork
5. **Pull Request** vers branche `develop`

### Standards de code
- **Backend** : Java 17, Spring Boot standards
- **Frontend** : TypeScript, ESLint, Prettier
- **Commits** : Messages clairs et structurés

## 📝 Licence

Ce projet est sous licence MIT.

## 🆘 Support

Pour toute question ou problème :
- **Issues** : Créer une issue sur GitHub
- **Documentation** : Consulter les README spécifiques
- **Contact** : Via les issues du projet

---

**🎯 Prêt pour le déploiement en production !**
