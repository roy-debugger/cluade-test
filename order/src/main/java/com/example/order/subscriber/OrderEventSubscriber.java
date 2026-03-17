package com.example.order.subscriber;


import com.example.order.event.OrderCreatedEvent;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderEventSubscriber {

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("order")
                .onEvent(OrderCreatedEvent.class, this::handleOrderCreatedEvent)
                .build();
    }
    public void handleOrderCreatedEvent(DomainEventEnvelope<OrderCreatedEvent> event) {
        String aggregateId = event.getAggregateId();
        OrderCreatedEvent orderCreatedEvent = event.getEvent();
        log.info(aggregateId + " OrderCreatedEvent: " + orderCreatedEvent.getOrderId());
    }
    
}