package com.company.{{SERVICE_NAME}}.handler;

import com.company.event.order.OrderCreatedEvent;
import com.company.{{SERVICE_NAME}}.service.{{SERVICE_NAME_PASCAL}}Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Handles order-related events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final {{SERVICE_NAME_PASCAL}}Service service;

    @KafkaListener(
        topics = "${app.kafka.topics.order-events:order-events}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleOrderEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event.getEventId());

        try {
            service.processOrderCreated(event);
            log.info("Successfully processed OrderCreatedEvent: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Failed to process OrderCreatedEvent: {}", event.getEventId(), e);
            throw e;
        }
    }
}
