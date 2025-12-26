# Claude Code Instructions - Microservices

## Project Overview

Java Spring Boot Microservices with:
- **Architecture**: Domain-Driven Design (DDD)
- **Communication**: REST (sync) + Kafka (async)
- **Database**: PostgreSQL per service
- **Shared**: common-lib, event-lib

## Project Structure

```
java-spring-microservices/
├── services/           # Microservices
├── shared/
│   ├── common-lib/     # DTOs, Exceptions, Utils
│   └── event-lib/      # Domain Events
├── infrastructure/     # Docker, K8s
└── tools/generators/   # Service generator
```

## Service Types

| Type | Use Case | Has DB | Has REST | Publishes Events |
|------|----------|--------|----------|------------------|
| core | Business logic | Yes | Yes | Yes |
| event | Event consumer | Maybe | No | Maybe |
| gateway | API Gateway | No | Yes | No |
| bff | Aggregator | No | Yes | No |

## Creating New Service

```powershell
.\tools\generators\new-service.ps1 -Name "product" -Type "core"
```

## Package Structure (Core Service)

```
com.company.{service}/
├── domain/
│   ├── model/          # @Entity
│   ├── repository/     # Repository interfaces
│   └── service/        # Domain services
├── application/
│   ├── usecase/        # Application services
│   ├── dto/            # DTOs
│   └── mapper/         # MapStruct
├── infrastructure/
│   ├── persistence/    # JPA implementations
│   ├── messaging/      # Kafka
│   └── client/         # Feign clients
└── interfaces/
    ├── rest/           # Controllers
    └── event/          # Event handlers
```

## Code Conventions

### Entity (Domain Layer)
```java
@Entity
@Table(name = "orders")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Domain logic methods here
}
```

### Use Case (Application Layer)
```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateOrderUseCase {
    private final OrderRepository repository;
    private final EventPublisher publisher;

    public OrderDto execute(CreateOrderRequest request) {
        // 1. Validate
        // 2. Create entity
        // 3. Save
        // 4. Publish event
        return toDto(saved);
    }
}
```

### Feign Client (Infrastructure Layer)
```java
@FeignClient(name = "user-service", fallbackFactory = UserClientFallback.class)
public interface UserServiceClient {
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable Long id);
}
```

### Event Publisher
```java
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafka;

    public void publishOrderCreated(Order order) {
        var event = OrderCreatedEvent.builder()
            .orderId(order.getId())
            .build();
        kafka.send("order-events", event);
    }
}
```

## Inter-Service Communication

### Sync (REST via Feign)
- Use for queries, immediate responses
- Implement Circuit Breaker
- Have fallback strategies

### Async (Kafka Events)
- Use for state changes, notifications
- Events in shared event-lib
- Idempotent consumers

## Testing

```bash
# Unit tests
mvn test

# Integration tests with Testcontainers
mvn verify

# All services
mvn test -f pom.xml
```

## Commands

```bash
# Start infrastructure
cd infrastructure/docker && docker-compose up -d

# Create new service
.\tools\generators\new-service.ps1 -Name "product" -Type "core"

# Build all
mvn clean package

# Run specific service
cd services/user-service && mvn spring-boot:run
```
