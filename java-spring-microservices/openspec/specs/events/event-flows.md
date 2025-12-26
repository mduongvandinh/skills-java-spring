# Event Flows Specification

## Overview

This specification defines Kafka topics, event schemas, and flow patterns
for asynchronous communication between services.

---

### Requirement: Event Topics

Each domain SHALL have its own Kafka topic.

#### Scenario: Topic naming

Given a domain "order"
When creating the Kafka topic
Then the topic name SHALL be `order-events`
And the DLT name SHALL be `order-events.DLT`

| Domain | Topic | DLT |
|--------|-------|-----|
| User | `user-events` | `user-events.DLT` |
| Order | `order-events` | `order-events.DLT` |
| Payment | `payment-events` | `payment-events.DLT` |

---

### Requirement: Event Base Structure

All events SHALL extend BaseEvent.

#### Scenario: Event creation

Given any domain event
When creating the event
Then it SHALL include:
- `eventId` (UUID string)
- `eventType` (enum value)
- `timestamp` (Instant)
- Domain-specific data

```java
public interface DomainEvent {
    String getEventId();
    String getEventType();
    Instant getTimestamp();
}

@Data
@SuperBuilder
public abstract class BaseEvent implements DomainEvent {
    private String eventId;
    private String eventType;
    private Instant timestamp;
}

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends BaseEvent {
    private Long orderId;
    private Long userId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
}
```

---

### Requirement: Event Publishing

Events SHALL be published after successful database commit.

#### Scenario: Order created

Given an order is successfully saved
When the transaction commits
Then OrderCreatedEvent SHALL be published to `order-events`

```java
@Transactional
public OrderDto execute(CreateOrderRequest request) {
    var order = repository.save(createOrder(request));

    // Publish after save succeeds
    kafkaTemplate.send("order-events", OrderCreatedEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType("ORDER_CREATED")
        .timestamp(Instant.now())
        .orderId(order.getId())
        .userId(order.getUserId())
        .build());

    return toDto(order);
}
```

---

### Requirement: Consumer Groups

Each service SHALL have its own consumer group.

#### Scenario: Multiple consumers

Given order-events topic
When notification-service and audit-service consume
Then each SHALL use unique groupId
And both SHALL receive all messages

```yaml
# notification-service
spring:
  kafka:
    consumer:
      group-id: notification-service

# audit-service
spring:
  kafka:
    consumer:
      group-id: audit-service
```

---

### Requirement: Event Flow - User Registration

User registration SHALL trigger welcome notification.

#### Scenario: New user registers

```
1. Client → User Service: POST /api/users
2. User Service → user_db: Save user
3. User Service → Kafka: Publish UserCreatedEvent
4. Kafka → Notification Service: Consume event
5. Notification Service: Send welcome email
6. Kafka → Audit Service: Consume event
7. Audit Service: Log user creation
```

---

### Requirement: Event Flow - Order Placement

Order placement SHALL trigger payment and notifications.

#### Scenario: New order placed

```
1. Client → Gateway: POST /api/orders
2. Gateway → Order Service: Route request
3. Order Service → User Service: GET /api/users/{id} (Feign)
4. Order Service → Payment Service: POST /api/payments (Feign)
5. Payment Service → payment_db: Save payment
6. Payment Service → Kafka: Publish PaymentCompletedEvent
7. Order Service → order_db: Save order
8. Order Service → Kafka: Publish OrderCreatedEvent
9. Kafka → Notification Service: Send confirmation
10. Kafka → Inventory Service: Update stock
```

---

### Requirement: Retry Policy

Failed messages SHALL be retried with backoff.

#### Scenario: Consumer fails

Given an event handler throws exception
When retry is attempted
Then it SHALL retry 3 times
And wait 1 second between retries
And send to DLT after all retries fail

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
    var recoverer = new DeadLetterPublishingRecoverer(template);
    var backoff = new FixedBackOff(1000L, 3); // 1s delay, 3 retries
    return new DefaultErrorHandler(recoverer, backoff);
}
```

---

### Requirement: Idempotent Consumers

All event handlers SHALL be idempotent.

#### Scenario: Duplicate event

Given event with ID "abc-123" was processed
When same event arrives again
Then handler SHALL skip processing
And log warning message

```java
private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

@KafkaListener(topics = "order-events")
public void handle(OrderCreatedEvent event) {
    if (!processedEvents.add(event.getEventId())) {
        log.warn("Duplicate event: {}", event.getEventId());
        return;
    }
    // Process event
}
```
