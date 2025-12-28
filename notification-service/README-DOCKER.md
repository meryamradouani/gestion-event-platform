# 🐳 Guide Docker pour le Microservice de Notifications

## 📋 Prérequis

- Docker Desktop installé et en cours d'exécution
- Maven installé (pour le build local)
- Java 17

## 🚀 Démarrage rapide

### Option 1 : Script PowerShell (Windows)

```powershell
.\start-docker.ps1
```

### Option 2 : Commandes manuelles

1. **Build l'application Spring Boot :**
```bash
mvn clean package -DskipTests
```

2. **Démarrer tous les services :**
```bash
docker-compose up -d
```

3. **Voir les logs du service de notifications :**
```bash
docker-compose logs -f notification-service
```

## 📦 Services inclus

Le `docker-compose.yml` inclut :

1. **Zookeeper** (port 2181) - Coordination pour Kafka
2. **Kafka** (port 9092) - Message broker
3. **MySQL** (port 3306) - Base de données
   - Database: `notification_db`
   - Username: `root`
   - Password: `root`
4. **Notification Service** (port 8080) - Microservice Spring Boot

## 🔍 Vérification

### Vérifier que tous les services sont démarrés :

```bash
docker-compose ps
```

### Vérifier les logs :

```bash
# Logs du service de notifications
docker-compose logs -f notification-service

# Tous les logs
docker-compose logs -f
```

### Vérifier que Kafka fonctionne :

```bash
# Lister les topics
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# Vérifier le consumer group
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group notification-service --describe
```

### Tester l'API :

```bash
# Health check (si actuator est configuré)
curl http://localhost:8080/actuator/health

# Liste des événements
curl http://localhost:8080/api/events/latest
```

## 🧪 Tester avec Kafka

### Envoyer un message de test :

```bash
# Test registrations.created
echo '{"userId":123,"eventId":456,"eventTitle":"Concert de Jazz","registrationDate":"2024-12-25T20:00:00"}' | docker exec -i kafka kafka-console-producer --bootstrap-server localhost:9092 --topic registrations.created

# Test user.tokens.updated
echo '{"userId":123,"fcmToken":"abc123xyz","deviceType":"android","deviceInfo":"Samsung Galaxy S23","createdAt":"2025-12-25T14:00:00","updatedAt":"2025-12-25T14:00:00"}' | docker exec -i kafka kafka-console-producer --bootstrap-server localhost:9092 --topic user.tokens.updated

# Test events.created
echo '{"eventId":123,"eventTitle":"Concert de Jazz","eventDescription":"Un concert exceptionnel...","creatorId":456,"eventDate":"2024-12-25T20:00:00"}' | docker exec -i kafka kafka-console-producer --bootstrap-server localhost:9092 --topic events.created
```

## 🛑 Arrêter les services

```bash
docker-compose down
```

Pour supprimer aussi les volumes (base de données) :

```bash
docker-compose down -v
```

## 🔧 Configuration

### Variables d'environnement

Le service Spring Boot utilise les variables suivantes (définies dans `docker-compose.yml`) :

- `SPRING_DATASOURCE_URL` : URL de connexion MySQL
- `SPRING_DATASOURCE_USERNAME` : Username MySQL
- `SPRING_DATASOURCE_PASSWORD` : Password MySQL
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` : Adresse Kafka (kafka:29092 dans Docker)
- `SPRING_PROFILES_ACTIVE` : Profile Spring (docker)

### Fichiers de configuration

- `application.yaml` : Configuration pour développement local
- `application-docker.yaml` : Configuration pour Docker (utilisée avec le profile `docker`)

## 🐛 Dépannage

### Le service ne démarre pas

1. Vérifier les logs :
```bash
docker-compose logs notification-service
```

2. Vérifier que MySQL et Kafka sont prêts :
```bash
docker-compose ps
```

3. Rebuild l'image :
```bash
docker-compose build --no-cache notification-service
docker-compose up -d notification-service
```

### Kafka n'est pas accessible

Vérifier que Kafka est bien démarré :
```bash
docker exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092
```

### MySQL n'est pas accessible

Vérifier la connexion :
```bash
docker exec -it mysql-notification-db mysql -uroot -proot -e "SHOW DATABASES;"
```

## 📝 Notes importantes

- Le service Spring Boot attend que Kafka et MySQL soient **healthy** avant de démarrer (healthchecks configurés)
- Les tables sont créées automatiquement grâce à `ddl-auto: update`
- Le fichier Firebase doit être présent dans `src/main/resources/` pour que les notifications push fonctionnent


