# Dockerfile pour le microservice de notifications Spring Boot
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .

# Copier le code source
COPY src ./src

# Build l'application (Maven téléchargera les dépendances automatiquement)
RUN mvn clean package -DskipTests -B

# Image finale
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/gestion-event-backend-*.jar app.jar

# Créer le répertoire pour les ressources
RUN mkdir -p /app/src/main/resources

# Exposer le port
EXPOSE 8080

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]

