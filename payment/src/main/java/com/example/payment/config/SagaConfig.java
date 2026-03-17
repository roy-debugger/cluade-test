package com.example.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.payment.service.PaymentService;

import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.commands.consumer.CommandDispatcherFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SagaConfig {
    
    private final PaymentService paymentService;
    
    @Bean
    public CommandDispatcher paymentCommandDispatcher(CommandDispatcherFactory commandDispatcherFactory) {
        return commandDispatcherFactory.make("paymentService", paymentService.commandHandlers());
    }
    
}