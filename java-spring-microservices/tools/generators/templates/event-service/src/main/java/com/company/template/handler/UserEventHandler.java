package com.company.{{SERVICE_NAME}}.handler;

import com.company.event.user.UserCreatedEvent;
import com.company.{{SERVICE_NAME}}.service.{{SERVICE_NAME_PASCAL}}Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Handles user-related events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {

    private final {{SERVICE_NAME_PASCAL}}Service service;

    @KafkaListener(
        topics = "${app.kafka.topics.user-events:user-events}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleUserEvent(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent: {}", event.getEventId());

        try {
            service.processUserCreated(event);
            log.info("Successfully processed UserCreatedEvent: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Failed to process UserCreatedEvent: {}", event.getEventId(), e);
            throw e; // Let error handler deal with it
        }
    }
}
