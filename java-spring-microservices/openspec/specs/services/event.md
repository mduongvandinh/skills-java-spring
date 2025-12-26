# Event Service Specification

## Overview

Event services consume Kafka messages and perform actions.
They have no REST API and no database (or read-only database).

---

### Requirement: Package Structure

An event service SHALL have a simplified package structure.

#### Scenario: New event service created

Given a new event service named "notification"
When the service is generated
Then the package structure SHALL be:
```
com.company.notification/
├── config/           # Kafka configuration
├── handlers/         # Event handlers
└── services/         # Processing logic (email, SMS, etc.)
```

---

### Requirement: Event Handler

Event handlers SHALL consume messages from Kafka topics.

#### Scenario: Handling an event

Given an event service listening to "order-events"
When implementing the handler
Then it SHALL:
- Use `@KafkaListener` annotation
- Specify topic and groupId
- Log the event processing

```java
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventHandler {
    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event.getOrderId());
        notificationService.sendOrderConfirmation(event);
    }
}
```

---

### Requirement: Idempotency

Event handlers SHALL be idempotent to handle duplicate messages.

#### Scenario: Duplicate event received

Given an event with eventId "abc-123" was already processed
When the same event is received again
Then the handler SHALL skip processing
And log a warning

```java
@KafkaListener(topics = "order-events", groupId = "${spring.kafka.consumer.group-id}")
public void handleOrderCreated(OrderCreatedEvent event) {
    if (processedEvents.contains(event.getEventId())) {
        log.warn("Duplicate event ignored: {}", event.getEventId());
        return;
    }

    // Process event
    notificationService.sendOrderConfirmation(event);
    processedEvents.add(event.getEventId());
}
```

---

### Requirement: Error Handling

Failed events SHALL be retried and eventually sent to DLT.

#### Scenario: Event processing fails

Given an event handler throws an exception
When the error occurs
Then the event SHALL be retried 3 times with 1 second delay
And if all retries fail, sent to Dead Letter Topic (DLT)

```java
@Bean
public DefaultErrorHandler errorHandler() {
    return new DefaultErrorHandler(
        new DeadLetterPublishingRecoverer(kafkaTemplate),
        new FixedBackOff(1000L, 3)
    );
}
```

---

### Requirement: Multiple Event Types

An event service MAY consume from multiple topics.

#### Scenario: Notification service consumes all events

Given notification service needs to handle multiple event types
When implementing handlers
Then each handler SHALL be in its own class or method

```java
@Component
public class OrderEventHandler {
    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void handleOrderEvent(OrderCreatedEvent event) { }
}

@Component
public class UserEventHandler {
    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void handleUserEvent(UserCreatedEvent event) { }
}
```

---

### Requirement: No REST API

Event services SHALL NOT expose REST endpoints.

#### Scenario: Health check only

Given an event service
When checking its HTTP endpoints
Then only actuator health endpoints SHALL be available
And no business API endpoints SHALL exist
