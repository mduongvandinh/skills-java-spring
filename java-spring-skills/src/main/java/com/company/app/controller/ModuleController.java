package com.company.app.controller;

import com.company.app.config.ModuleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller to check which modules are currently enabled.
 */
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleProperties moduleProperties;

    @GetMapping("/modules")
    public ResponseEntity<Map<String, Object>> getModuleStatus() {
        return ResponseEntity.ok(Map.of(
            "modules", Map.of(
                "postgresql", Map.of(
                    "enabled", moduleProperties.getPostgresql().isEnabled(),
                    "description", "PostgreSQL Database with JPA/Hibernate"
                ),
                "redis", Map.of(
                    "enabled", moduleProperties.getRedis().isEnabled(),
                    "description", "Redis Cache"
                ),
                "kafka", Map.of(
                    "enabled", moduleProperties.getKafka().isEnabled(),
                    "description", "Apache Kafka Messaging"
                ),
                "rabbitmq", Map.of(
                    "enabled", moduleProperties.getRabbitmq().isEnabled(),
                    "description", "RabbitMQ Messaging"
                ),
                "oauth2", Map.of(
                    "enabled", moduleProperties.getOauth2().isEnabled(),
                    "description", "OAuth2 Authentication"
                )
            ),
            "hint", "Use environment variables like MODULE_REDIS_ENABLED=true to enable modules"
        ));
    }
}
