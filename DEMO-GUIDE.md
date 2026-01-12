# Guide de Démonstration - Plateforme de Gestion d'Événements

## 📋 Préparation avant la démonstration

### 1. Démarrer tous les services Docker
```bash
docker-compose up -d
```

### 2. Vérifier que tous les conteneurs sont en cours d'exécution
```bash
docker ps
```

Vous devriez voir 8 conteneurs:
- `mysql-container`
- `zookeeper`
- `kafka`
- `auth-service`
- `registration-service`
- `event-service`
- `notification-service`
- `profil-service`
- `api-gateway`

---

## 🔍 Tests de Vérification

### Option Rapide: Script Automatisé
```bash
verify-services.bat
```
Ce script vérifie automatiquement tous les services.

---

## 📝 Tests Manuels Détaillés

### 1️⃣ Vérification de MySQL

**Vérifier que MySQL est accessible:**
```bash
docker exec mysql-container mysql -uroot -proot -e "SHOW DATABASES;"
```

**Résultat attendu:** Vous devriez voir les bases de données:
- `db_auth_service`
- `event_db`
- `notification_db`
- `profile_db`
- `registration_db`

---

### 2️⃣ Vérification de Kafka

**Lister les topics Kafka:**
```bash
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

**Résultat attendu:** Vous devriez voir les topics:
- `event-created`
- `user-registered`
- `notification-sent`

---

### 3️⃣ Vérification des Services (Health Checks)

**Auth Service (Port 8081):**
```bash
curl http://localhost:8081/actuator/health
```

**Registration Service (Port 8082):**
```bash
curl http://localhost:8082/actuator/health
```

**Event Service (Port 8083):**
```bash
curl http://localhost:8083/actuator/health
```

**Notification Service (Port 8080):**
```bash
curl http://localhost:8080/actuator/health
```

**Profile Service (Port 8085):**
```bash
curl http://localhost:8085/actuator/health
```

**API Gateway (Port 8888):**
```bash
curl http://localhost:8888/actuator/health
```

**Résultat attendu pour chaque service:**
```json
{"status":"UP"}
```

---

## 🧪 Tests Fonctionnels

### 4️⃣ Test du Service d'Authentification

**Créer un nouvel utilisateur:**
```bash
curl -X POST http://localhost:8081/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"demo_user\",\"email\":\"demo@example.com\",\"password\":\"Demo123!\"}"
```

**Se connecter:**
```bash
curl -X POST http://localhost:8081/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"demo_user\",\"password\":\"Demo123!\"}"
```

**Résultat attendu:** Un token JWT

---

### 5️⃣ Test du Service d'Événements

**Créer un événement:**
```bash
curl -X POST http://localhost:8083/api/events ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Conférence Tech 2026\",\"description\":\"Une conférence sur les microservices\",\"date\":\"2026-03-15\",\"location\":\"Paris\"}"
```

**Récupérer tous les événements:**
```bash
curl http://localhost:8083/api/events
```

**Récupérer un événement spécifique (remplacer {id} par l'ID réel):**
```bash
curl http://localhost:8083/api/events/{id}
```

---

### 6️⃣ Test du Service d'Inscription

**S'inscrire à un événement:**
```bash
curl -X POST http://localhost:8082/api/registrations ^
  -H "Content-Type: application/json" ^
  -d "{\"eventId\":1,\"userId\":1,\"registrationDate\":\"2026-01-12\"}"
```

**Récupérer toutes les inscriptions:**
```bash
curl http://localhost:8082/api/registrations
```

---

### 7️⃣ Test du Service de Profil

**Créer un profil utilisateur:**
```bash
curl -X POST http://localhost:8085/api/profiles ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"firstName\":\"Jean\",\"lastName\":\"Dupont\",\"phoneNumber\":\"+33612345678\"}"
```

**Récupérer un profil:**
```bash
curl http://localhost:8085/api/profiles/1
```

---

### 8️⃣ Vérification de la Communication Kafka

**Vérifier les logs du service de notification pour voir les messages Kafka:**
```bash
docker logs notification-service --tail 50
```

**Résultat attendu:** Vous devriez voir des messages indiquant la réception d'événements Kafka

---

## 🌐 Tests via API Gateway

**Health check via Gateway:**
```bash
curl http://localhost:8888/actuator/health
```

**Accéder aux services via Gateway:**

**Auth Service via Gateway:**
```bash
curl http://localhost:8888/auth-service/actuator/health
```

**Event Service via Gateway:**
```bash
curl http://localhost:8888/event-service/actuator/health
```

**Registration Service via Gateway:**
```bash
curl http://localhost:8888/registration-service/actuator/health
```

---

## 📊 Commandes de Monitoring

### Vérifier l'utilisation des ressources
```bash
docker stats
```

### Vérifier les logs d'un service spécifique
```bash
docker logs auth-service
docker logs event-service
docker logs notification-service
docker logs registration-service
docker logs profil-service
docker logs api-gateway
```

### Vérifier les logs en temps réel
```bash
docker logs -f event-service
```

---

## 🔄 Démonstration du Flux Complet

### Scénario: Création d'un événement et inscription

**1. Créer un utilisateur:**
```bash
curl -X POST http://localhost:8081/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"alice\",\"email\":\"alice@example.com\",\"password\":\"Alice123!\"}"
```

**2. Créer un événement:**
```bash
curl -X POST http://localhost:8083/api/events ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Workshop Docker\",\"description\":\"Atelier pratique sur Docker et Kubernetes\",\"date\":\"2026-02-20\",\"location\":\"Lyon\"}"
```

**3. Vérifier que l'événement a été créé:**
```bash
curl http://localhost:8083/api/events
```

**4. S'inscrire à l'événement:**
```bash
curl -X POST http://localhost:8082/api/registrations ^
  -H "Content-Type: application/json" ^
  -d "{\"eventId\":1,\"userId\":1,\"registrationDate\":\"2026-01-12\"}"
```

**5. Vérifier les logs de notification (Kafka):**
```bash
docker logs notification-service --tail 20
```

---

## 🛑 Arrêter les services après la démonstration

```bash
docker-compose down
```

Pour supprimer également les volumes (données):
```bash
docker-compose down -v
```

---

## ✅ Checklist de Démonstration

- [ ] Tous les conteneurs Docker sont démarrés
- [ ] MySQL est accessible et contient toutes les bases de données
- [ ] Kafka est accessible et les topics sont créés
- [ ] Tous les services répondent au health check
- [ ] Auth Service: Inscription et connexion fonctionnent
- [ ] Event Service: Création et récupération d'événements fonctionnent
- [ ] Registration Service: Inscription à un événement fonctionne
- [ ] Profile Service: Création de profil fonctionne
- [ ] Notification Service: Reçoit les messages Kafka
- [ ] API Gateway: Route correctement vers les services

---

## 🎯 Points Clés à Mentionner au Professeur

1. **Architecture Microservices**: Chaque service est indépendant avec sa propre base de données
2. **Communication Asynchrone**: Utilisation de Kafka pour la communication entre services
3. **API Gateway**: Point d'entrée unique pour tous les services
4. **Containerisation**: Tous les services sont dockerisés
5. **Health Checks**: Monitoring de l'état de chaque service
6. **Scalabilité**: Architecture permettant de scaler chaque service indépendamment
