@echo off
setlocal

echo Starting Minikube...
minikube status >nul 2>&1
if %ERRORLEVEL% neq 0 (
    minikube start
)

echo Configuring Docker to use Minikube's daemon...
@FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env --shell cmd') DO @%%i

echo Building microservices...
docker build -t auth-service:latest ./auth-service
docker build -t event-service:latest ./event-service
docker build -t notification-service:latest ./notification-service
docker build -t profil-service:latest ./profil-service
docker build -t registration-service:latest ./registration-service
docker build -t api-gateway:latest ./api-gateway

echo Applying Kubernetes manifests...
kubectl apply -f k8s/

echo Deployment complete!
echo Use "kubectl get pods" to check the status.
echo Use "minikube service api-gateway --url" to get the API Gateway URL.

endlocal
