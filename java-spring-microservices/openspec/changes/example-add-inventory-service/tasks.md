# Tasks: Add Inventory Service

## Implementation Checklist

### 1. Generate Service
- [ ] Run: `.\tools\generators\new-service.ps1 -Name "inventory" -Type "core"`
- [ ] Update port to 8085 in application.yml
- [ ] Add to docker-compose.yml

### 2. Database Schema
- [ ] Create `products` table migration
- [ ] Create `stock_movements` table migration
- [ ] Configure inventory_db in docker-compose

### 3. Domain Layer
- [ ] Create `Product` entity
- [ ] Create `StockMovement` entity
- [ ] Create `ProductRepository`
- [ ] Create `StockMovementRepository`

### 4. Application Layer
- [ ] Create `GetStockUseCase`
- [ ] Create `UpdateStockUseCase`
- [ ] Create `ReserveStockUseCase`
- [ ] Create DTOs: `StockDto`, `UpdateStockRequest`

### 5. Events
- [ ] Add `StockUpdatedEvent` to event-lib
- [ ] Add `LowStockAlertEvent` to event-lib
- [ ] Add `StockReservedEvent` to event-lib
- [ ] Implement `InventoryEventPublisher`
- [ ] Implement `OrderEventHandler` (consume OrderCreatedEvent)

### 6. API Endpoints
- [ ] GET /api/inventory/{productId} - Get stock level
- [ ] PUT /api/inventory/{productId} - Update stock
- [ ] POST /api/inventory/{productId}/reserve - Reserve stock

### 7. Infrastructure
- [ ] Add Kubernetes deployment manifest
- [ ] Add to kustomization.yaml
- [ ] Update CI/CD pipeline

### 8. Integration
- [ ] Add Feign client in order-service
- [ ] Update order creation flow to check stock

### 9. Testing
- [ ] Unit tests for use cases
- [ ] Integration tests for event handling
- [ ] Contract tests for Feign client

### 10. Documentation
- [ ] Add OpenAPI spec to shared/api-contracts/
- [ ] Update docs/architecture/event-flows.md
- [ ] Merge specs to openspec/specs/ after completion
