package com.company.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global filter for logging all requests and adding trace ID.
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        log.info("[{}] {} {} from {}",
            traceId,
            request.getMethod(),
            request.getPath(),
            request.getRemoteAddress()
        );

        ServerHttpRequest mutatedRequest = request.mutate()
            .header(TRACE_ID_HEADER, traceId)
            .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
            .doOnSuccess(aVoid -> log.info("[{}] Completed", traceId))
            .doOnError(error -> log.error("[{}] Error: {}", traceId, error.getMessage()));
    }

    @Override
    public int getOrder() {
        return -100; // Run early
    }
}
