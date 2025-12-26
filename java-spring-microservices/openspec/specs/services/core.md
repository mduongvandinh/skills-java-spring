# Core Service Specification

## Overview

Core services contain business logic with their own PostgreSQL database.
They communicate synchronously via REST/Feign and asynchronously via Kafka.

---

### Requirement: Package Structure

A core service SHALL follow DDD package structure.

#### Scenario: New core service created

Given a new core service named "product"
When the service is generated
Then the package structure SHALL be:
```
com.company.product/
├── domain/           # Entities, Value Objects, Repositories
├── application/      # Use Cases, DTOs, Mappers
├── infrastructure/   # JPA, Kafka, Feign implementations
└── interfaces/       # REST Controllers, Event Handlers
```

---

### Requirement: Use Case Pattern

Business logic SHALL be encapsulated in Use Case classes.

#### Scenario: Creating a use case

Given a business operation "CreateOrder"
When implementing the use case
Then the class SHALL:
- Be annotated with `@Service`, `@RequiredArgsConstructor`, `@Transactional`
- Have a single public `execute()` method
- Inject repositories, clients, and publishers via constructor

```java
@Service
@RequiredArgsConstructor
@Transactional
public class CreateOrderUseCase {
    private final OrderRepository repository;
    private final UserServiceClient userClient;
    private final OrderEventPublisher publisher;

    public OrderDto execute(CreateOrderRequest request) {
        // 1. Validate with external service
        var user = userClient.getUserById(request.getUserId());

        // 2. Create entity
        var order = Order.builder()
            .userId(user.getId())
            .items(request.getItems())
            .build();
        order = repository.save(order);

        // 3. Publish event
        publisher.publishOrderCreated(order);

        return toDto(order);
    }
}
```

---

### Requirement: Feign Client with Fallback

Inter-service communication SHALL use Feign with circuit breaker fallback.

#### Scenario: Calling another service

Given a core service needs data from user-service
When implementing the Feign client
Then the client SHALL have a FallbackFactory

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

---

### Requirement: Event Publishing

State changes SHALL be published as domain events to Kafka.

#### Scenario: Entity created

Given an entity is successfully created
When the use case completes
Then a domain event SHALL be published with:
- Unique eventId (UUID)
- Timestamp
- Relevant entity data

```java
kafkaTemplate.send("order-events", OrderCreatedEvent.builder()
    .eventId(UUID.randomUUID().toString())
    .orderId(order.getId())
    .userId(order.getUserId())
    .timestamp(Instant.now())
    .build());
```

---

### Requirement: REST Controller

Controllers SHALL delegate to use cases and return ApiResponse.

#### Scenario: POST endpoint

Given a create operation
When implementing the controller
Then it SHALL:
- Use `@Valid` for request validation
- Delegate to use case
- Return `ApiResponse<T>` wrapper

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        var order = createOrderUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
```

---

### Requirement: Database Isolation

Each core service SHALL have its own database.

#### Scenario: Database configuration

Given a core service named "order"
When configuring the database
Then the database name SHALL be `order_db`
And no other service SHALL access this database directly
