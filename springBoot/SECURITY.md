# 🔐 Configuration de Sécurité - Backend

## Variables d'Environnement

### Configuration Initiale

1. **Copier le fichier d'exemple**:

   ```bash
   cd springBoot
   cp .env.example .env
   ```

2. **Modifier `.env` avec vos valeurs**:
   ```bash
   # Ouvrir avec votre éditeur
   nano .env
   # ou
   code .env
   ```

### Variables Requises

#### Base de Données

```properties
DB_HOST=localhost              # Hôte PostgreSQL
DB_PORT=5432                   # Port PostgreSQL
DB_NAME=internship             # Nom de la base de données
DB_USERNAME=postgres           # Utilisateur PostgreSQL
DB_PASSWORD=votre_mot_de_passe # ⚠️ CHANGEZ CECI!
```

#### JWT (JSON Web Token)

```properties
JWT_SECRET=votre_secret_jwt_256_bits_minimum
```

**⚠️ IMPORTANT**: Générez un secret sécurisé:

```bash
# Linux/Mac
openssl rand -hex 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

#### Email (SMTP)

```properties
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=votre_email@gmail.com
MAIL_PASSWORD=votre_app_password
```

**Pour Gmail**: Créez un [App Password](https://myaccount.google.com/apppasswords)

#### CORS (Cross-Origin Resource Sharing)

```properties
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

Ajoutez vos domaines de production séparés par des virgules.

#### Logging

```properties
LOG_LEVEL=INFO  # Options: DEBUG, INFO, WARN, ERROR
```

**Recommandations**:

- Développement: `DEBUG`
- Production: `INFO` ou `WARN`

---

## 🛡️ Fonctionnalités de Sécurité Implémentées

### 1. Rate Limiting

- **Limite**: 100 requêtes par minute par IP
- **Protection**: Prévient les attaques DDoS et brute force
- **Fichier**: `RateLimitFilter.java`

### 2. Headers de Sécurité

Headers HTTP automatiquement ajoutés:

- `Strict-Transport-Security` (HSTS)
- `X-Content-Type-Options`
- `X-Frame-Options`
- `X-XSS-Protection`
- `Content-Security-Policy`
- `Referrer-Policy`
- `Permissions-Policy`

**Fichier**: `SecurityHeadersConfig.java`

### 3. Configuration CORS

- Origines autorisées configurables
- Credentials supportés
- Headers personnalisés autorisés

**Fichier**: `CorsConfig.java`

### 4. Validation de Fichiers

Protection contre les fichiers malveillants:

- ✅ Vérification du type MIME
- ✅ Validation de la signature du fichier (magic numbers)
- ✅ Limite de taille (10 MB par défaut)
- ✅ Sanitization des noms de fichiers
- ✅ Protection contre path traversal

**Fichier**: `FileValidator.java`

**Types autorisés**:

- PDFs: `application/pdf`
- Images: `image/jpeg`, `image/png`, `image/webp`

---

## 🚀 Démarrage

### Avec Variables d'Environnement

#### Option 1: Fichier .env (Recommandé)

Le fichier `.env` est automatiquement chargé par Spring Boot.

```bash
# Assurez-vous que .env existe et est configuré
mvn spring-boot:run
```

#### Option 2: Variables d'Environnement Système

**Linux/Mac**:

```bash
export DB_PASSWORD="votre_password"
export JWT_SECRET="votre_secret"
export MAIL_PASSWORD="votre_mail_password"
mvn spring-boot:run
```

**Windows PowerShell**:

```powershell
$env:DB_PASSWORD="votre_password"
$env:JWT_SECRET="votre_secret"
$env:MAIL_PASSWORD="votre_mail_password"
mvn spring-boot:run
```

#### Option 3: Arguments de Ligne de Commande

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--DB_PASSWORD=xxx --JWT_SECRET=yyy"
```

---

## 🔒 Bonnes Pratiques de Sécurité

### ✅ À FAIRE

1. **Ne jamais commiter `.env`**

   - Déjà ajouté au `.gitignore`
   - Vérifier: `git status` ne doit pas montrer `.env`

2. **Utiliser des secrets forts**

   - JWT: Minimum 256 bits (32 caractères hex)
   - Passwords: Complexes et uniques

3. **Changer les secrets en production**

   - Ne jamais utiliser les valeurs d'exemple
   - Utiliser un gestionnaire de secrets (AWS Secrets Manager, Azure Key Vault, etc.)

4. **Limiter les origines CORS**

   - En production, spécifier uniquement vos domaines
   - Éviter `*` (wildcard)

5. **Activer HTTPS en production**
   - Les headers HSTS nécessitent HTTPS
   - Utiliser un certificat SSL/TLS valide

### ❌ À ÉVITER

1. ❌ Commiter des secrets dans Git
2. ❌ Utiliser des mots de passe faibles
3. ❌ Exposer les stacktraces en production
4. ❌ Désactiver la validation de fichiers
5. ❌ Utiliser `LOG_LEVEL=DEBUG` en production

---

## 🧪 Tester la Sécurité

### Test Rate Limiting

```bash
# Envoyer 101 requêtes rapidement
for i in {1..101}; do
  curl http://localhost:8080/login
done
# La 101ème devrait retourner 429 (Too Many Requests)
```

### Test CORS

```bash
curl -H "Origin: http://localhost:5173" \
     -H "Access-Control-Request-Method: POST" \
     -X OPTIONS http://localhost:8080/login -v
```

### Test Headers de Sécurité

```bash
curl -I http://localhost:8080/login
# Vérifier la présence des headers de sécurité
```

---

## 📝 Logs et Monitoring

### Activer les Logs de Sécurité

Dans `.env`:

```properties
LOG_LEVEL=DEBUG
```

### Logs Importants à Surveiller

- Tentatives de connexion échouées
- Rate limiting dépassé
- Fichiers rejetés par validation
- Erreurs JWT

---

## 🐳 Docker (Voir Phase 4)

Les variables d'environnement seront gérées via:

- `docker-compose.yml`
- Fichier `.env` à la racine
- Docker secrets (production)

---

## 📞 Support

En cas de problème:

1. Vérifier que `.env` existe et est configuré
2. Vérifier les logs: `tail -f logs/spring-boot.log`
3. Vérifier les variables: `echo $DB_PASSWORD`

---

**Version**: 1.0.0  
**Dernière mise à jour**: 2026-01-02
