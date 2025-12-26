# Inventory Events Specification (Delta)

## ADDED Requirements

### Requirement: Inventory Events Topic

Inventory events SHALL be published to dedicated topic.

#### Scenario: Topic configuration

Given inventory-service publishes events
When configuring Kafka
Then topic SHALL be `inventory-events`
And DLT SHALL be `inventory-events.DLT`

---

### Requirement: StockUpdatedEvent

System SHALL publish event when stock changes.

#### Scenario: Stock updated

Given stock level changes
When update completes
Then StockUpdatedEvent SHALL be published

```java
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class StockUpdatedEvent extends BaseEvent {
    private Long productId;
    private String productName;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String reason; // SALE, RESTOCK, ADJUSTMENT
}
```

```json
{
  "eventId": "uuid",
  "eventType": "STOCK_UPDATED",
  "timestamp": "2024-12-26T10:00:00Z",
  "productId": 123,
  "productName": "Widget",
  "previousQuantity": 50,
  "newQuantity": 45,
  "reason": "SALE"
}
```

---

### Requirement: StockReservedEvent

System SHALL publish event when stock is reserved.

#### Scenario: Stock reserved for order

Given stock is reserved for an order
When reservation completes
Then StockReservedEvent SHALL be published

```java
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class StockReservedEvent extends BaseEvent {
    private Long productId;
    private Long orderId;
    private Integer quantity;
    private Instant expiresAt; // Reservation expiry
}
```

---

### Requirement: LowStockAlertEvent

System SHALL publish alert when stock is low.

#### Scenario: Low stock detected

Given available stock < 10 units
When stock update completes
Then LowStockAlertEvent SHALL be published
And notification-service SHALL consume it

```java
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LowStockAlertEvent extends BaseEvent {
    private Long productId;
    private String productName;
    private Integer currentQuantity;
    private Integer threshold;
}
```

---

### Requirement: Consume OrderCreatedEvent

Inventory service SHALL consume order events.

#### Scenario: Order placed

Given OrderCreatedEvent is published
When inventory-service consumes it
Then stock SHALL be reserved for each item
And StockReservedEvent SHALL be published

```java
@KafkaListener(topics = "order-events", groupId = "inventory-service")
public void handleOrderCreated(OrderCreatedEvent event) {
    for (var item : event.getItems()) {
        reserveStockUseCase.execute(item.getProductId(), item.getQuantity(), event.getOrderId());
    }
}
```

---

## MODIFIED Requirements

### Event Flows Table (docs/architecture/event-flows.md)

Add to existing table:

| Topic | Publisher | Consumers |
|-------|-----------|-----------|
| `inventory-events` | Inventory Service | Order, Notification, Audit |
