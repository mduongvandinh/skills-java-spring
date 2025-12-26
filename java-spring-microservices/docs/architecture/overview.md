# Architecture Overview

## System Architecture

```
                                    ┌─────────────────┐
                                    │   API Gateway   │
                                    │    (Port 8080)  │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
           ┌───────────────┐        ┌───────────────┐        ┌───────────────┐
           │  User Service │        │ Order Service │        │Payment Service│
           │  (Port 8081)  │        │  (Port 8082)  │        │  (Port 8083)  │
           └───────┬───────┘        └───────┬───────┘        └───────┬───────┘
                   │                        │                        │
                   ▼                        ▼                        ▼
           ┌───────────────┐        ┌───────────────┐        ┌───────────────┐
           │   user_db     │        │   order_db    │        │  payment_db   │
           └───────────────┘        └───────────────┘        └───────────────┘
                   │                        │                        │
                   └────────────────────────┼────────────────────────┘
                                            │
                                    ┌───────▼───────┐
                                    │     Kafka     │
                                    │  (Port 9092)  │
                                    └───────┬───────┘
                                            │
                              ┌─────────────┴─────────────┐
                              │                           │
                              ▼                           ▼
                     ┌───────────────┐           ┌───────────────┐
                     │ Notification  │           │    Audit      │
                     │   Service     │           │   Service     │
                     └───────────────┘           └───────────────┘
```

## Service Types

### 1. Core Services
Business logic services with their own databases.

| Service | Port | Database | Publishes Events |
|---------|------|----------|------------------|
| User Service | 8081 | user_db | UserCreated, UserUpdated |
| Order Service | 8082 | order_db | OrderCreated, OrderShipped |
| Payment Service | 8083 | payment_db | PaymentCompleted, PaymentFailed |

### 2. Event Services
Consume events, no REST API.

| Service | Consumes | Actions |
|---------|----------|---------|
| Notification Service | All events | Send email, SMS, push |
| Audit Service | All events | Log to audit database |

### 3. Gateway Service
Single entry point for all APIs.

- Route requests to appropriate services
- Rate limiting
- Authentication
- Circuit breaker

## Communication Patterns

### Synchronous (REST)
- Used for: Queries, immediate responses
- Technology: Spring Cloud OpenFeign
- Pattern: Request-Response

### Asynchronous (Kafka)
- Used for: State changes, notifications
- Technology: Spring Kafka
- Pattern: Publish-Subscribe

## Data Flow Example: Create Order

```
1. Client → Gateway: POST /api/orders
2. Gateway → Order Service: Route request
3. Order Service → User Service: GET /api/users/{id} (Feign)
4. Order Service → Payment Service: POST /api/payments (Feign)
5. Order Service → Kafka: Publish OrderCreatedEvent
6. Order Service → Gateway → Client: Return order
7. Kafka → Notification Service: Send confirmation email
8. Kafka → Audit Service: Log order creation
```

## Deployment

### Development
- Docker Compose for local development
- All services run on localhost with different ports

### Production
- Kubernetes deployment
- Each service has 2+ replicas
- Horizontal Pod Autoscaler for scaling
- Ingress for external access

## Technology Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.2, Spring Cloud 2023 |
| Database | PostgreSQL 16 |
| Messaging | Apache Kafka |
| Caching | Redis |
| Container | Docker |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
