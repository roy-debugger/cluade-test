package com.example.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.inventory.service.InventoryService;

import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.commands.consumer.CommandDispatcherFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SagaConfig {
    
    private final InventoryService inventoryService;

    @Bean
    public CommandDispatcher inventoryCommandDispatcher(CommandDispatcherFactory commandDispatcherFactory) {
        return commandDispatcherFactory.make("inventoryService", inventoryService.commandHandlers());
    }
}