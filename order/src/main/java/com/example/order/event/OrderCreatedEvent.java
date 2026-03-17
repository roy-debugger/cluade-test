package com.example.order.event;

import io.eventuate.tram.events.common.DomainEvent;

public class OrderCreatedEvent implements DomainEvent {
    private String orderId;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}