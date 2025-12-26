package com.company.event.payment;

import com.company.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event published when a payment is completed.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedEvent extends BaseEvent {

    private static final String EVENT_TYPE = "PAYMENT_COMPLETED";

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String transactionId;

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
