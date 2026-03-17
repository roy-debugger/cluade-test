package com.example.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.order.subscriber.OrderEventSubscriber;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;

@Configuration
public class EventuateTramConfig {

  @Bean
  public DomainEventDispatcher domainEventDispatcher(DomainEventDispatcherFactory
                  domainEventDispatcherFactory, OrderEventSubscriber target) {
    return domainEventDispatcherFactory.make("cg_order",
                                             target.domainEventHandlers());
  }

  @Bean
  public OrderEventSubscriber orderEventSubscriber() {
    return new OrderEventSubscriber();
  }

}
