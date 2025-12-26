package com.company.event.order;

import com.company.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Event published when a new order is created.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends BaseEvent {

    private static final String EVENT_TYPE = "ORDER_CREATED";

    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private String currency;
    private List<OrderItemDto> items;

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
    }
}
