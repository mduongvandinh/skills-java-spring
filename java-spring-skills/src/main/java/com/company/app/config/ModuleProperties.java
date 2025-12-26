package com.company.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for optional modules.
 * Controls which features are enabled at runtime.
 *
 * Usage in application.yml:
 *   modules:
 *     redis:
 *       enabled: true
 */
@Data
@ConfigurationProperties(prefix = "modules")
public class ModuleProperties {

    private ModuleConfig postgresql = new ModuleConfig(true);
    private ModuleConfig redis = new ModuleConfig(false);
    private ModuleConfig kafka = new ModuleConfig(false);
    private ModuleConfig rabbitmq = new ModuleConfig(false);
    private ModuleConfig oauth2 = new ModuleConfig(false);

    @Data
    public static class ModuleConfig {
        private boolean enabled;

        public ModuleConfig() {
            this.enabled = false;
        }

        public ModuleConfig(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
