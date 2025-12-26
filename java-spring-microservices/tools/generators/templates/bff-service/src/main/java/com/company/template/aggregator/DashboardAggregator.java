package com.company.{{SERVICE_NAME}}.aggregator;

import com.company.{{SERVICE_NAME}}.client.OrderServiceClient;
import com.company.{{SERVICE_NAME}}.client.UserServiceClient;
import com.company.{{SERVICE_NAME}}.dto.DashboardResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * Aggregates data from multiple services for dashboard view.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAggregator {

    private final UserServiceClient userClient;
    private final OrderServiceClient orderClient;

    @Cacheable(value = "dashboard", key = "#userId")
    @CircuitBreaker(name = "dashboard", fallbackMethod = "getDashboardFallback")
    public DashboardResponse getDashboard(Long userId) {
        log.info("Aggregating dashboard for user: {}", userId);

        // Parallel calls to multiple services
        var userFuture = CompletableFuture.supplyAsync(() ->
            userClient.getUserById(userId));
        var ordersFuture = CompletableFuture.supplyAsync(() ->
            orderClient.getOrdersByUserId(userId));

        // Wait for all and combine
        CompletableFuture.allOf(userFuture, ordersFuture).join();

        return DashboardResponse.builder()
            .user(userFuture.join())
            .recentOrders(ordersFuture.join())
            .build();
    }

    // Fallback when services are unavailable
    public DashboardResponse getDashboardFallback(Long userId, Throwable t) {
        log.warn("Dashboard fallback for user: {}, reason: {}", userId, t.getMessage());
        return DashboardResponse.builder()
            .user(null)
            .recentOrders(Collections.emptyList())
            .errorMessage("Some data is temporarily unavailable")
            .build();
    }
}
