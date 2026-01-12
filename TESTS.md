# Tests de Vérification des Services

## Services à Tester

- [x] Auth Service (Port 8081)
- [x] Registration Service (Port 8082)
- [x] Event Service (Port 8083)
- [x] Notification Service (Port 8080)
- [x] Profile Service (Port 8085)
- [x] API Gateway (Port 8888)
- [x] MySQL Database
- [x] Kafka

## Commandes de Test Rapides

### 1. Vérifier tous les conteneurs
```bash
docker ps
```

### 2. Tester tous les services (Script automatique)
```bash
verify-services.bat
```

### 3. Tests individuels des services

#### MySQL
```bash
docker exec mysql-container mysql -uroot -proot -e "SHOW DATABASES;"
```

#### Kafka Topics
```bash
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

#### Health Checks
```bash
curl http://localhost:8081/actuator/health  # Auth
curl http://localhost:8082/actuator/health  # Registration
curl http://localhost:8083/actuator/health  # Event
curl http://localhost:8080/actuator/health  # Notification
curl http://localhost:8085/actuator/health  # Profile
curl http://localhost:8888/actuator/health  # Gateway
```

### 4. Tests Fonctionnels

#### Créer un utilisateur
```bash
curl -X POST http://localhost:8081/api/auth/register -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"Test123!\"}"
```

#### Créer un événement
```bash
curl -X POST http://localhost:8083/api/events -H "Content-Type: application/json" -d "{\"name\":\"Demo Event\",\"description\":\"Test\",\"date\":\"2026-02-15\",\"location\":\"Paris\"}"
```

#### Lister les événements
```bash
curl http://localhost:8083/api/events
```

## Voir le Guide Complet
Consultez [DEMO-GUIDE.md](DEMO-GUIDE.md) pour tous les détails.
