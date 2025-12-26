# QUICKSTART - Microservices

## Yêu cầu

- Java 21+ ([Download](https://adoptium.net/))
- Maven 3.9+ ([Download](https://maven.apache.org/download.cgi))
- Docker Desktop ([Download](https://www.docker.com/products/docker-desktop/))

## Bước 1: Build shared libraries

```powershell
cd d:\1.AI\3.projects\java\skills\java-spring-microservices

# Build common-lib và event-lib
mvn install -pl shared/common-lib,shared/event-lib -DskipTests
```

## Bước 2: Khởi động Infrastructure

```powershell
cd infrastructure\docker

# Start PostgreSQL, Kafka, Redis
docker-compose up -d

# Kiểm tra
docker-compose ps
```

**Kết quả mong đợi:**
```
NAME            STATUS
ms-postgres     Up
ms-kafka        Up
ms-redis        Up
ms-kafka-ui     Up
```

## Bước 3: Tạo service đầu tiên

```powershell
cd ..\..

# Tạo User Service
.\tools\generators\new-service.ps1 -Name "user" -Type "core"

# Tạo Order Service
.\tools\generators\new-service.ps1 -Name "order" -Type "core"

# Tạo Notification Service (event consumer)
.\tools\generators\new-service.ps1 -Name "notification" -Type "event"
```

## Bước 4: Copy Gateway Service

```powershell
# Gateway không cần generate, copy trực tiếp
Copy-Item -Path "tools\generators\templates\gateway-service" -Destination "services\gateway-service" -Recurse
```

## Bước 5: Chạy services

Mở 3 terminal khác nhau:

**Terminal 1 - User Service:**
```powershell
cd services\user-service
mvn spring-boot:run
```

**Terminal 2 - Order Service:**
```powershell
cd services\order-service
mvn spring-boot:run
```

**Terminal 3 - Gateway:**
```powershell
cd services\gateway-service
mvn spring-boot:run
```

## Bước 6: Test APIs

```powershell
# Qua Gateway (port 8080)
curl http://localhost:8080/api/users
curl http://localhost:8080/api/orders

# Trực tiếp (debug)
curl http://localhost:8081/api/users
curl http://localhost:8082/api/orders
```

---

## Service Ports

| Service | Port |
|---------|------|
| Gateway | 8080 |
| User Service | 8081 |
| Order Service | 8082 |
| Payment Service | 8083 |
| Notification | 8084 |
| Kafka UI | 8090 |

## Useful URLs

- **Kafka UI**: http://localhost:8090
- **Gateway Health**: http://localhost:8080/actuator/health
- **Gateway Routes**: http://localhost:8080/actuator/gateway/routes

---

## Lỗi thường gặp

### "Port already in use"
```powershell
netstat -ano | findstr :8081
taskkill /PID <pid> /F
```

### "Cannot connect to Kafka"
```powershell
docker-compose restart kafka
```

### "Database connection refused"
```powershell
docker-compose logs postgres
docker-compose restart postgres
```

---

## Tiếp theo

1. Đọc [Architecture Overview](docs/architecture/overview.md)
2. Xem [Event Flows](docs/architecture/event-flows.md)
3. Review [API Contracts](shared/api-contracts/)
