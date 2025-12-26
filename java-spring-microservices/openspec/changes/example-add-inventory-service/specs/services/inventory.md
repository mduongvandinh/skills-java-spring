# Inventory Service Specification (Delta)

## ADDED Requirements

### Requirement: Service Type

Inventory service SHALL be a Core Service type.

#### Scenario: Service characteristics

Given inventory-service
When checking its type
Then it SHALL:
- Have its own database (inventory_db)
- Expose REST API
- Publish events to Kafka
- Consume events from Kafka

---

### Requirement: Package Structure

Inventory service SHALL follow DDD package structure.

#### Scenario: Package organization

```
com.company.inventory/
├── domain/
│   ├── Product.java
│   ├── StockMovement.java
│   └── ProductRepository.java
├── application/
│   ├── GetStockUseCase.java
│   ├── UpdateStockUseCase.java
│   ├── ReserveStockUseCase.java
│   └── dto/
├── infrastructure/
│   ├── persistence/
│   ├── kafka/
│   │   ├── InventoryEventPublisher.java
│   │   └── OrderEventHandler.java
│   └── config/
└── interfaces/
    └── InventoryController.java
```

---

### Requirement: Stock Management API

The service SHALL expose stock management endpoints.

#### Scenario: Get stock level

Given product with ID 123 exists
When GET /api/inventory/123 is called
Then return current stock level

```json
{
  "success": true,
  "data": {
    "productId": 123,
    "productName": "Widget",
    "quantity": 50,
    "reservedQuantity": 5,
    "availableQuantity": 45
  }
}
```

#### Scenario: Update stock

Given product with ID 123 exists
When PUT /api/inventory/123 is called with quantity change
Then stock SHALL be updated
And StockUpdatedEvent SHALL be published

#### Scenario: Reserve stock

Given product with ID 123 has available quantity >= 5
When POST /api/inventory/123/reserve with quantity 5
Then reservedQuantity SHALL increase by 5
And StockReservedEvent SHALL be published

---

### Requirement: Optimistic Locking

Stock updates SHALL use optimistic locking.

#### Scenario: Concurrent updates

Given two concurrent update requests
When both try to update same product
Then one SHALL succeed
And the other SHALL receive 409 Conflict

```java
@Entity
public class Product {
    @Version
    private Long version;
}
```

---

### Requirement: Low Stock Alert

System SHALL alert when stock is low.

#### Scenario: Stock below threshold

Given product stock falls below 10 units
When stock update completes
Then LowStockAlertEvent SHALL be published

```java
if (product.getAvailableQuantity() < LOW_STOCK_THRESHOLD) {
    publisher.publishLowStockAlert(product);
}
```
