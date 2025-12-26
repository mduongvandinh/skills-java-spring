package com.company.{{SERVICE_NAME}}.service;

import com.company.event.order.OrderCreatedEvent;
import com.company.event.user.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that processes events.
 * Implement your business logic here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class {{SERVICE_NAME_PASCAL}}Service {

    // Inject external service clients here (email, SMS, etc.)

    public void processUserCreated(UserCreatedEvent event) {
        log.info("Processing user created: userId={}, username={}",
            event.getUserId(), event.getUsername());

        // TODO: Implement your logic
        // Example: Send welcome email, create user profile, etc.
    }

    public void processOrderCreated(OrderCreatedEvent event) {
        log.info("Processing order created: orderId={}, userId={}, amount={}",
            event.getOrderId(), event.getUserId(), event.getTotalAmount());

        // TODO: Implement your logic
        // Example: Send order confirmation, update analytics, etc.
    }
}
