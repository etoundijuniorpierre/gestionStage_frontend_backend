# Stage 1: Build avec Maven pour Spring Boot 3.3.13 et Java 17
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copier pom.xml et télécharger les dépendances (layer cached)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Builder l'application (skip tests pour build plus rapide)
RUN mvn clean package -DskipTests

# Stage 2: Runtime avec Java 17
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Créer les répertoires nécessaires pour les uploads de photos de profil
RUN mkdir -p uploads/profilsUser

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
RUN chown -R spring:spring /app

USER spring:spring

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 9080 (votre port configuré dans application.properties)
EXPOSE 9080

# Health check pour Render (vérifie que l'API répond)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=5 \
    CMD wget --quiet --tries=1 --spider http://localhost:9080/api/health || exit 1

# Lancer l'application avec les options JVM optimisées
# Utilisation de sh -c pour permettre l'expansion des variables d'environnement
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Djava.net.preferIPv4Stack=true -jar app.jar"]
