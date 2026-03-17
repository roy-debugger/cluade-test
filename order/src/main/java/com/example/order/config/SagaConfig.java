package com.example.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.example.order.service.OrderService;

import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.commands.consumer.CommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import lombok.RequiredArgsConstructor;

@Configuration
@Import({SagaOrchestratorConfiguration.class})
@RequiredArgsConstructor
public class SagaConfig {
    
    private final OrderService orderService;
    
    @Bean
    public CommandDispatcher orderCommandDispatcher(CommandDispatcherFactory commandDispatcherFactory) {
        return commandDispatcherFactory.make("orderService", orderService.commandHandlers());
    }
}