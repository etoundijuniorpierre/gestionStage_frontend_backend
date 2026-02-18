# API - Internship Management Application

RESTful API for the internship management platform, allowing students, companies, teachers and administrators to manage the entire internship process.

**Base URL**: `http://localhost:8080`

## Authentification

This API uses JSON Web Token **(JWT) authentication**. Include the token in the request header:


## Technology Stack

- **Backend**: Spring Boot, Spring Security, JPA/Hibernate
- **Mapping**: MapStruct
- **Authentification**: JWT (io.jsonwebtoken)
- **Base de données**: PostgreSQL
- **Utilitaires**: Lombok
- **Sécurité**: Spring Security
- **Version Java**: 17+
- **Frontend**: HTML, React.js

## Main Endpoints 

### 1. Authentication

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/registration/registerStudent` | Inscription d'un étudiant |
| POST | `/registration/registerTeacher` | Inscription d'un enseignant |
| POST | `/registration/registerEnterprise` | Inscription d'une entreprise |
| POST | `/login` | Connexion utilisateur |

### 2. Student Management

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| PUT | `/api/student/{application_id}/updateStudentStatus` | Mettre à jour le statut de l'étudiant |
| POST | `/api/student/{offer_id}/createApplication` | Créer une candidature |
| GET | `/api/student/pendingApplicationsOfStudent` | Candidatures en attente de l'étudiant |

### 3. Companies Management

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| PUT | `/api/enterprise/application/{id}/validate` | Valider une candidature |
| POST | `/api/enterprise/createOffer` | Créer une offre de stage |
| GET | `/api/enterprise/listOfOffers` | Liste des offres de l'entreprise |

### 4. Teachers Management

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| PUT | `/api/teacher/offers/{id}/validate` | Valider une offre et convention |
| GET | `/api/teacher/offersApprovedByTeacher` | Offres approuvées par l'enseignant |

### 5. Administration

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| PUT | `/api/admin/Enterprise/{id}/approve` | Approuver une entreprise |
| GET | `/api/admin/internships.xlsx` | Télécharger Excel des stages |
| GET | `/api/admin/allStudent` | Tous les étudiants |

## Examples of Use

### connection

```http
POST http://localhost:8080/login
Content-Type: application/json

{
  "email": "admin@email.com",
  "password": "admin"
}

// Response
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
## Create Offer

```
POST http://localhost:8080/api/enterprise/createOffer
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "title": "Développeur FullStack",
  "description": "Développement d'applications web",
  "domain": "Informatique",
  "job": "Développeur",
  "numberOfPlaces": 2,
  "paying": true,
  "remote": false
}
``` 

## Apply to an offer

```

POST http://localhost:8080/api/student/1/createApplication
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: multipart/form-data

// Form data:
// cv: [PDF file]
// coverLetter: [PDF file]

```

## Data Model 

### StudentRegistrationRequestDto

```
{
  "name": "string",
  "firstName": "string",
  "email": "string",
  "password": "string",
  "sector": "string",
  "languages": ["string"],
  "department": "string",
  "githubLink": "string",
  "linkedinLink": "string"
}

```

### EnterpriseRegistrationRequestDto

```
{
  "name": "string",
  "email": "string",
  "matriculation": "string",
  "password": "string",
  "contact": "string",
  "location": "string",
  "sectorOfActivity": "string",
  "country": "string",
  "city": "string",
  "logo": blob
}

```

###  TeacherRegistrationRequestDto

```
{
"name": "string",
"firstName": "string",
"email": "string",
"password": "string",
"department": "string"
}

```

### OfferRequestDto

```
{
"title": "string",
"description": "string",
"domain": "string",
"job": "string",
"requirements": "string",
"startDate": "date",
"endDate": "date",
"numberOfPlaces": 0,
"paying": true,
"remote": true,
"typeOfInternship": "string"
}
```

*© 2025 - Internship Management Application*