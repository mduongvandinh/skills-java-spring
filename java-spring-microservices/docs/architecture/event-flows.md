# Event Flows

## Event Topics

| Topic | Publisher | Consumers |
|-------|-----------|-----------|
| `user-events` | User Service | Notification, Audit |
| `order-events` | Order Service | Payment, Notification, Audit |
| `payment-events` | Payment Service | Order, Notification, Audit |

## Event Schemas

All events are defined in `shared/event-lib`:

```
com.company.event/
├── DomainEvent.java      # Base interface
├── BaseEvent.java        # Abstract base class
├── user/
│   ├── UserCreatedEvent.java
│   └── UserUpdatedEvent.java
├── order/
│   ├── OrderCreatedEvent.java
│   ├── OrderShippedEvent.java
│   └── OrderDeliveredEvent.java
└── payment/
    ├── PaymentCompletedEvent.java
    └── PaymentFailedEvent.java
```

## Flow: User Registration

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    Client    │────▶│ User Service │────▶│    Kafka     │
└──────────────┘     └──────────────┘     └──────┬───────┘
                                                  │
                            ┌─────────────────────┼─────────────────────┐
                            ▼                     ▼                     ▼
                   ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
                   │ Notification │     │    Audit     │     │   Analytics  │
                   │   Service    │     │   Service    │     │   Service    │
                   └──────────────┘     └──────────────┘     └──────────────┘
                            │
                            ▼
                   Send welcome email
```

**Event: UserCreatedEvent**
```json
{
  "eventId": "uuid",
  "eventType": "USER_CREATED",
  "timestamp": "2024-12-26T10:00:00Z",
  "userId": 123,
  "username": "john",
  "email": "john@example.com"
}
```

## Flow: Order Placement

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    Client    │────▶│Order Service │────▶│    Kafka     │
└──────────────┘     └──────┬───────┘     └──────┬───────┘
                            │                     │
                            │ Feign               │
                            ▼                     │
                   ┌──────────────┐               │
                   │   Payment    │               │
                   │   Service    │───────────────┤
                   └──────────────┘               │
                                                  │
                   ┌──────────────────────────────┴─────────┐
                   ▼                     ▼                  ▼
          ┌──────────────┐     ┌──────────────┐   ┌──────────────┐
          │ Notification │     │    Audit     │   │  Inventory   │
          │   Service    │     │   Service    │   │   Service    │
          └──────────────┘     └──────────────┘   └──────────────┘
```

**Sequence:**
1. Client submits order
2. Order Service validates with User Service (Feign)
3. Order Service requests payment (Feign)
4. Payment Service processes payment
5. Payment Service publishes PaymentCompletedEvent
6. Order Service publishes OrderCreatedEvent
7. Notification Service sends confirmation
8. Inventory Service updates stock

## Error Handling

### Retry Policy
```java
new FixedBackOff(1000L, 3)  // 3 retries, 1 second apart
```

### Dead Letter Queue
Failed messages after retries go to:
- `user-events.DLT`
- `order-events.DLT`
- `payment-events.DLT`

### Idempotency
All consumers should be idempotent:
```java
if (eventRepository.existsByEventId(event.getEventId())) {
    log.warn("Duplicate event: {}", event.getEventId());
    return;
}
```

## Monitoring

- **Kafka UI**: http://localhost:8090
- **Consumer lag**: Monitor via Kafka metrics
- **Failed events**: Alert on DLT messages
