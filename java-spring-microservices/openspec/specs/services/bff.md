# BFF Service Specification

## Overview

Backend for Frontend (BFF) services aggregate data from multiple core services
to provide optimized responses for specific frontend applications.

---

### Requirement: Package Structure

A BFF service SHALL have an aggregation-focused structure.

#### Scenario: New BFF service created

Given a new BFF service named "web-bff"
When the service is generated
Then the package structure SHALL be:
```
com.company.webbff/
├── config/           # Feign, async configuration
├── clients/          # Feign clients to core services
├── aggregators/      # Aggregation logic
├── dto/              # Response DTOs (aggregated)
└── controllers/      # REST endpoints
```

---

### Requirement: Parallel Service Calls

BFF SHALL make parallel calls to multiple services.

#### Scenario: Dashboard data aggregation

Given a dashboard needs user info, recent orders, and notifications
When the BFF endpoint is called
Then all three service calls SHALL execute in parallel
And results SHALL be combined into a single response

```java
@Service
@RequiredArgsConstructor
public class DashboardAggregator {
    private final UserServiceClient userClient;
    private final OrderServiceClient orderClient;
    private final NotificationClient notificationClient;
    private final ExecutorService executor;

    public DashboardDto getDashboard(Long userId) {
        CompletableFuture<UserDto> userFuture = CompletableFuture
            .supplyAsync(() -> userClient.getUserById(userId), executor);
        CompletableFuture<List<OrderDto>> ordersFuture = CompletableFuture
            .supplyAsync(() -> orderClient.getRecentOrders(userId), executor);
        CompletableFuture<List<NotificationDto>> notificationsFuture = CompletableFuture
            .supplyAsync(() -> notificationClient.getUnread(userId), executor);

        CompletableFuture.allOf(userFuture, ordersFuture, notificationsFuture).join();

        return DashboardDto.builder()
            .user(userFuture.join())
            .recentOrders(ordersFuture.join())
            .notifications(notificationsFuture.join())
            .build();
    }
}
```

---

### Requirement: Feign Clients

BFF SHALL use Feign clients with fallbacks for all core services.

#### Scenario: Core service unavailable

Given the order-service is unavailable
When the BFF makes a call
Then the fallback SHALL return empty data or cached data
And the overall response SHALL still succeed with partial data

```java
@FeignClient(name = "order-service", fallbackFactory = OrderClientFallback.class)
public interface OrderServiceClient {
    @GetMapping("/api/orders/user/{userId}/recent")
    List<OrderDto> getRecentOrders(@PathVariable Long userId);
}

@Component
public class OrderClientFallback implements FallbackFactory<OrderServiceClient> {
    @Override
    public OrderServiceClient create(Throwable cause) {
        return userId -> Collections.emptyList(); // Return empty, not fail
    }
}
```

---

### Requirement: Aggregated DTOs

BFF SHALL define its own DTOs combining data from multiple services.

#### Scenario: Dashboard response

Given a dashboard endpoint
When defining the response DTO
Then it SHALL combine data from multiple services

```java
@Data
@Builder
public class DashboardDto {
    private UserDto user;
    private List<OrderDto> recentOrders;
    private List<NotificationDto> notifications;
    private DashboardStats stats;
}

@Data
@Builder
public class DashboardStats {
    private int totalOrders;
    private BigDecimal totalSpent;
    private int unreadNotifications;
}
```

---

### Requirement: Timeout Configuration

BFF SHALL have appropriate timeouts for aggregation.

#### Scenario: Slow service response

Given one service takes longer than expected
When the timeout is reached
Then the BFF SHALL use fallback data
And not block other parallel calls

```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 2000
        readTimeout: 5000

resilience4j:
  timelimiter:
    instances:
      default:
        timeoutDuration: 3s
```

---

### Requirement: No Database

BFF services SHALL NOT have their own database.

#### Scenario: Data persistence

Given a BFF service
When it needs data
Then it SHALL always fetch from core services
And MAY cache responses in Redis for performance

---

### Requirement: Frontend-Specific Optimization

Each BFF SHALL be optimized for its target frontend.

#### Scenario: Mobile vs Web BFF

Given different frontend requirements
When implementing BFFs
Then mobile-bff MAY return smaller payloads
And web-bff MAY include more detailed data

```java
// Mobile BFF - minimal data
@GetMapping("/dashboard")
public MobileDashboardDto getMobileDashboard(@RequestParam Long userId) {
    return aggregator.getMobileDashboard(userId); // Smaller payload
}

// Web BFF - full data
@GetMapping("/dashboard")
public WebDashboardDto getWebDashboard(@RequestParam Long userId) {
    return aggregator.getFullDashboard(userId); // Complete payload
}
```
