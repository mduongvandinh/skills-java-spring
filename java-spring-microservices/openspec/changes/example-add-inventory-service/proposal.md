# Proposal: Add Inventory Service

## Summary

Create a new inventory-service to manage product stock levels and integrate with order-service.

## Rationale

- Order service needs real-time stock validation
- Inventory management is a separate bounded context
- Stock updates should be decoupled via events

## Scope

### In Scope
- Create inventory-service (core type)
- Stock management APIs (GET, PUT stock levels)
- Consume OrderCreatedEvent to reduce stock
- Publish StockUpdatedEvent, LowStockAlertEvent

### Out of Scope
- Warehouse management (future)
- Supplier integration (future)
- Physical inventory counting

## Service Interactions

```
                    ┌─────────────────┐
                    │  Order Service  │
                    └────────┬────────┘
                             │ OrderCreatedEvent
                             ▼
                    ┌─────────────────┐
                    │Inventory Service│
                    └────────┬────────┘
                             │
              ┌──────────────┴──────────────┐
              ▼                              ▼
    StockUpdatedEvent              LowStockAlertEvent
              │                              │
              ▼                              ▼
    ┌─────────────────┐           ┌─────────────────┐
    │  Order Service  │           │Notification Svc │
    └─────────────────┘           └─────────────────┘
```

## Dependencies

- Kafka topics: `inventory-events`
- Database: `inventory_db`
- Consumes: `order-events`

## Risks

- Race conditions in stock updates (use optimistic locking)
- Event ordering issues (use partition key by productId)
