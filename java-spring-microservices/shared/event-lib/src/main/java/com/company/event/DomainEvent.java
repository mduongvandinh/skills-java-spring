package com.company.event;

import java.time.Instant;

/**
 * Base interface for all domain events.
 */
public interface DomainEvent {

    String getEventId();

    String getEventType();

    Instant getTimestamp();

    default String getAggregateType() {
        return this.getClass().getSimpleName().replace("Event", "");
    }
}
