# Java Spring Microservices Skills

Bộ skill cho kiến trúc **Microservices** với Spring Boot, Spring Cloud.

> **Lưu ý**: Nếu dự án của bạn là **monolithic**, hãy sử dụng [java-spring-skills](../java-spring-skills) thay thế.

## Khi nào dùng Microservices?

| Điều kiện | Monolithic | Microservices |
|-----------|------------|---------------|
| Team size | 1-5 người | 5+ người |
| Độ phức tạp | Thấp-Trung | Cao |
| Scaling | Vertical | Horizontal |
| Deploy | Đơn giản | Phức tạp |
| Development speed | Nhanh ban đầu | Nhanh sau này |

## Cấu trúc dự án

```
java-spring-microservices/
├── services/                    # Các microservices
│   ├── user-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── notification-service/
│   └── gateway-service/
│
├── shared/                      # Shared libraries
│   ├── common-lib/              # DTOs, Utils, Exceptions
│   └── event-lib/               # Event schemas
│
├── infrastructure/              # Deployment configs
│   ├── docker/
│   └── kubernetes/
│
└── tools/                       # Scripts & generators
    ├── generators/
    └── scripts/
```

## Quick Start

### 1. Tạo service mới

```bash
# Windows PowerShell
.\tools\generators\new-service.ps1 -Name "product" -Type "core"

# Linux/macOS
./tools/generators/new-service.sh product core
```

### 2. Khởi động infrastructure

```bash
cd infrastructure/docker
docker-compose up -d
```

### 3. Chạy service

```bash
cd services/user-service
mvn spring-boot:run
```

## Service Types

| Type | Mô tả | Ví dụ |
|------|-------|-------|
| **core** | Business logic, có DB, publish events | user, order, payment |
| **event** | Consume events, không có REST API | notification, audit |
| **gateway** | API Gateway, routing, auth | gateway |
| **bff** | Backend for Frontend, aggregation | web-bff, mobile-bff |

## Tech Stack

- **Java 21** + **Spring Boot 3.2.x**
- **Spring Cloud** (Gateway, OpenFeign, Config)
- **PostgreSQL** (per service)
- **Kafka** (event streaming)
- **Redis** (caching, rate limiting)
- **Docker** + **Kubernetes**

## Documentation

- [QUICKSTART.md](QUICKSTART.md) - Bắt đầu trong 10 phút
- [SKILL.md](SKILL.md) - Skill documentation
- [docs/architecture/](docs/architecture/) - Architecture docs

## So sánh với java-spring-skills

| Feature | java-spring-skills | java-spring-microservices |
|---------|-------------------|---------------------------|
| Architecture | Monolithic | Microservices |
| Database | 1 shared | 1 per service |
| Communication | In-process | REST + Events |
| Deployment | Single JAR | Multiple containers |
| Complexity | Low | High |
| Learning curve | Easy | Steep |
