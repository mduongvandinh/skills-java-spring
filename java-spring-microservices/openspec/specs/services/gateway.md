# Gateway Service Specification

## Overview

The Gateway service is the single entry point for all API requests.
It handles routing, rate limiting, authentication, and circuit breaking.

---

### Requirement: Spring Cloud Gateway

The gateway SHALL use Spring Cloud Gateway (reactive).

#### Scenario: Gateway dependencies

Given a gateway service
When checking dependencies
Then it SHALL include:
- `spring-cloud-starter-gateway`
- `spring-cloud-starter-circuitbreaker-reactor-resilience4j`

---

### Requirement: Route Configuration

Routes SHALL be configured in application.yml.

#### Scenario: Routing to services

Given requests to `/api/users/**`
When the gateway receives the request
Then it SHALL route to `user-service`

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: user-service
                fallbackUri: forward:/fallback/user

        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: order-service
                fallbackUri: forward:/fallback/order
```

---

### Requirement: Rate Limiting

The gateway SHALL implement rate limiting.

#### Scenario: Too many requests

Given a client exceeds 100 requests per second
When the next request arrives
Then the gateway SHALL return HTTP 429 (Too Many Requests)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
```

---

### Requirement: Circuit Breaker

The gateway SHALL implement circuit breaker for each service.

#### Scenario: Service unavailable

Given user-service is not responding
When 5 consecutive requests fail
Then the circuit SHALL open
And requests SHALL be routed to fallback endpoint

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Void>> userFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.error("User service is temporarily unavailable"));
    }
}
```

---

### Requirement: Request Logging

The gateway SHALL log all incoming requests.

#### Scenario: Request received

Given any request to the gateway
When the request is processed
Then the gateway SHALL log:
- Request method and path
- Response status code
- Processing time

```java
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getPath().value();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = exchange.getResponse().getStatusCode().value();
            log.info("Request: {} {} - Status: {} - Duration: {}ms",
                exchange.getRequest().getMethod(), path, statusCode, duration);
        }));
    }
}
```

---

### Requirement: CORS Configuration

The gateway SHALL handle CORS for all services.

#### Scenario: Cross-origin request

Given a request from a different origin
When the gateway receives the request
Then it SHALL add appropriate CORS headers

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
            allowedHeaders: "*"
```

---

### Requirement: Port Configuration

The gateway SHALL run on port 8080.

#### Scenario: Gateway startup

Given the gateway service starts
When checking the port
Then it SHALL be listening on port 8080
