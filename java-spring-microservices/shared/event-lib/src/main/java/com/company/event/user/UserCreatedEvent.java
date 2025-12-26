package com.company.event.user;

import com.company.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when a new user is created.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCreatedEvent extends BaseEvent {

    private static final String EVENT_TYPE = "USER_CREATED";

    private Long userId;
    private String username;
    private String email;

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
