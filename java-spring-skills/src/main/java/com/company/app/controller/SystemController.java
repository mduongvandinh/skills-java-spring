package com.company.app.controller;

import com.company.app.config.ModuleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * System endpoints for health checks and module status.
 */
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final ModuleProperties moduleProperties;

    /**
     * Get current module status.
     *
     * @return Map of module names to their enabled status
     */
    @GetMapping("/modules")
    public Map<String, Boolean> getModuleStatus() {
        return Map.of(
            "postgresql", moduleProperties.getPostgresql().isEnabled(),
            "redis", moduleProperties.getRedis().isEnabled(),
            "kafka", moduleProperties.getKafka().isEnabled(),
            "rabbitmq", moduleProperties.getRabbitmq().isEnabled(),
            "oauth2", moduleProperties.getOauth2().isEnabled()
        );
    }

    /**
     * Simple health check endpoint.
     *
     * @return Health status with timestamp
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "timestamp", Instant.now().toString()
        );
    }
}
