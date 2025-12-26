package com.company.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent implements DomainEvent {

    private String eventId;
    private Instant timestamp;

    protected BaseEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public static String generateEventId() {
        return UUID.randomUUID().toString();
    }
}
