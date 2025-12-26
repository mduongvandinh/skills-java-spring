package com.company.app.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Conditional annotation to enable/disable beans based on module configuration.
 *
 * Usage:
 * <pre>
 * {@code
 * @Configuration
 * @ConditionalOnModuleEnabled("redis")
 * public class RedisConfig {
 *     // This config is only loaded when modules.redis.enabled=true
 * }
 * }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ConditionalOnModuleEnabled.OnModuleEnabledCondition.class)
public @interface ConditionalOnModuleEnabled {

    /**
     * The module name to check.
     * Corresponds to modules.{value}.enabled property.
     */
    String value();

    /**
     * Condition implementation that checks if a module is enabled.
     */
    class OnModuleEnabledCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Map<String, Object> attrs = metadata.getAnnotationAttributes(
                ConditionalOnModuleEnabled.class.getName());

            if (attrs == null) {
                return false;
            }

            String moduleName = (String) attrs.get("value");
            String property = "modules." + moduleName + ".enabled";

            return "true".equalsIgnoreCase(
                context.getEnvironment().getProperty(property, "false"));
        }
    }
}
