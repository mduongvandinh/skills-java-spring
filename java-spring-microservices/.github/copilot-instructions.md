# GitHub Copilot Instructions - Java Spring Microservices

## Project Overview
Enterprise microservices architecture with Spring Boot 3.2 and Spring Cloud.

## Architecture Pattern
- **Database per Service**: Each service owns its data
- **API Gateway**: Single entry point (Spring Cloud Gateway)
- **Event-Driven**: Async communication via Kafka
- **DDD**: Domain-Driven Design package structure

## Service Types

### 1. Core Service (Business Logic)
```
com.company.{service}/
├── domain/           # Entities, Repositories
├── application/      # Use Cases, DTOs
├── infrastructure/   # JPA, Kafka, Feign clients
└── interfaces/       # REST Controllers
```

### 2. Event Service (Kafka Consumer)
```
com.company.{service}/
├── config/           # Kafka configuration
├── handlers/         # Event handlers
└── services/         # Processing logic
```

### 3. Gateway Service (API Gateway)
- Routes requests to services
- Rate limiting, circuit breaker
- Authentication

### 4. BFF Service (Backend for Frontend)
- Aggregates multiple service calls
- Optimized for specific frontend

## Code Patterns

### Use Case (Application Layer)
```java
@Service
@RequiredArgsConstructor
@Transactional
public class CreateOrderUseCase {
    private final OrderRepository repository;
    private final UserServiceClient userClient;
    private final OrderEventPublisher publisher;

    public OrderDto execute(CreateOrderRequest request) {
        var user = userClient.getUserById(request.getUserId());
        var order = Order.builder()
            .userId(user.getId())
            .items(request.getItems())
            .build();
        order = repository.save(order);
        publisher.publishOrderCreated(order);
        return toDto(order);
    }
}
```

### Feign Client (Inter-Service Communication)
```java
@FeignClient(name = "user-service", fallbackFactory = UserClientFallback.class)
public interface UserServiceClient {
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable Long id);
}

@Component
public class UserClientFallback implements FallbackFactory<UserServiceClient> {
    @Override
    public UserServiceClient create(Throwable cause) {
        return id -> {
            throw new ServiceUnavailableException("User service unavailable");
        };
    }
}
```

### Event Publisher
```java
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(Order order) {
        kafkaTemplate.send("order-events", OrderCreatedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .orderId(order.getId())
            .userId(order.getUserId())
            .timestamp(Instant.now())
            .build());
    }
}
```

### Event Consumer
```java
@Component
@Slf4j
public class OrderEventHandler {
    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Idempotency check
        if (processedEvents.contains(event.getEventId())) {
            log.warn("Duplicate event: {}", event.getEventId());
            return;
        }
        // Process event
        sendNotification(event);
        processedEvents.add(event.getEventId());
    }
}
```

### Controller (Interface Layer)
```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        var order = createOrderUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
```

## Communication Rules

| Type | When to Use | Technology |
|------|-------------|------------|
| Synchronous | Queries, validations | REST + Feign |
| Asynchronous | State changes, notifications | Kafka |

## Key Files
- `docs/architecture/overview.md` - System architecture diagram
- `docs/architecture/event-flows.md` - Kafka topics and event flows
- `shared/api-contracts/` - OpenAPI specifications
- `shared/common-lib/` - Shared DTOs and exceptions
- `shared/event-lib/` - Domain event definitions

## Creating New Service
```powershell
# Core service with database
.\tools\generators\new-service.ps1 -Name "product" -Type "core"

# Event consumer service
.\tools\generators\new-service.ps1 -Name "analytics" -Type "event"

# BFF service
.\tools\generators\new-service.ps1 -Name "mobile" -Type "bff"
```

## Best Practices
1. Always implement circuit breaker fallbacks for Feign clients
2. Make event handlers idempotent
3. Use correlation IDs for distributed tracing
4. Keep services loosely coupled
5. Follow contract-first API design (OpenAPI in shared/api-contracts/)
