@echo off
chcp 65001 >nul
echo ========================================
echo   VERIFICATION DES SERVICES DOCKER
echo ========================================
echo.

echo [1] Vérification des conteneurs Docker en cours d'exécution...
echo ----------------------------------------
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo.

echo [2] Vérification de MySQL...
echo ----------------------------------------
docker exec mysql-container mysql -uroot -proot -e "SHOW DATABASES;" 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ✓ MySQL est accessible
    echo.
    echo Bases de données créées:
    docker exec mysql-container mysql -uroot -proot -e "SHOW DATABASES;" 2>nul | findstr "db_auth_service event_db notification_db profile_db registration_db"
) else (
    echo ✗ MySQL n'est pas accessible
)
echo.
echo [2b] Vérification des tables dans chaque base de données...
echo ----------------------------------------
set DB_LIST=db_auth_service event_db notification_db profile_db registration_db
for %%D in (%DB_LIST%) do (
    echo.
    echo Base de données: %%D
    docker exec mysql-container mysql -uroot -proot -e "SHOW TABLES FROM %%D;" 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Accès aux tables réussi pour %%D
    ) else (
        echo [ERREUR] Impossible de lister les tables pour %%D
    )
)
echo.

echo [3] Vérification de Kafka...
echo ----------------------------------------
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ✓ Kafka est accessible
    echo.
    echo Topics Kafka disponibles:
    docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 2>nul
) else (
    echo ✗ Kafka n'est pas accessible
)
echo.

echo [4] Vérification des services via Health Endpoints...
echo ----------------------------------------

echo Testing Auth Service (Port 8081)...
curl -s http://localhost:8081/actuator/health
if %ERRORLEVEL% EQU 0 (
    echo ✓ Auth Service est UP
) else (
    echo ✗ Auth Service n'est pas accessible
)
echo.

echo Testing Registration Service (Port 8082)...
curl -s http://localhost:8082/actuator/health
if %ERRORLEVEL% EQU 0 (
    echo ✓ Registration Service est UP
) else (
    echo ✗ Registration Service n'est pas accessible
)
echo.

echo Testing Event Service (Port 8083)...
curl -s http://localhost:8083/actuator/health
if %ERRORLEVEL% EQU 0 (
    echo ✓ Event Service est UP
) else (
    echo ✗ Event Service n'est pas accessible
)
echo.

echo Testing Notification Service (Port 8080)...
curl -s http://localhost:8080/actuator/health
if %ERRORLEVEL% EQU 0 (
    echo ✓ Notification Service est UP
) else (
    echo ✗ Notification Service n'est pas accessible
)
echo.

echo Testing Profile Service (Port 8085)...
curl -s http://localhost:8085/actuator/health
if %ERRORLEVEL% EQU 0 (
    echo ✓ Profile Service est UP
) else (
    echo ✗ Profile Service n'est pas accessible
)
echo.

echo Testing API Gateway (Port 8888)...
curl -s http://localhost:8888/actuator/health
if %ERRORLEVEL% EQU 0 (
    echo ✓ API Gateway est UP
) else (
    echo ✗ API Gateway n'est pas accessible
)
echo.

echo ========================================
echo   TESTS FONCTIONNELS
echo ========================================
echo.

echo [5] Test d'authentification (Auth Service)...
echo ----------------------------------------
echo Requête: POST http://localhost:8081/api/auth/register
curl -X POST http://localhost:8081/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo [6] Test de création d'événement (Event Service)...
echo ----------------------------------------
echo Requête: POST http://localhost:8083/api/events
curl -X POST http://localhost:8083/api/events ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test Event\",\"description\":\"Event de démonstration\",\"date\":\"2026-02-15\",\"location\":\"Salle A\"}"
echo.
echo.

echo [7] Test de récupération des événements (Event Service)...
echo ----------------------------------------
echo Requête: GET http://localhost:8083/api/events
curl -s http://localhost:8083/api/events
echo.
echo.

echo [8] Vérification des logs Kafka dans Notification Service...
echo ----------------------------------------
docker logs notification-service --tail 20 2>nul | findstr "Kafka"
echo.

echo ========================================
echo   VERIFICATION TERMINEE
echo ========================================
pause
