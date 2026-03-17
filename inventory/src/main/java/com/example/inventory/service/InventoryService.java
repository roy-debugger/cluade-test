package com.example.inventory.service;

import com.example.inventory.command.ReleaseInventoryCommand;
import com.example.inventory.command.ReserveInventoryCommand;
import com.example.inventory.domain.Inventory;
import com.example.inventory.repository.InventoryRepository;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    
    @Transactional
    public void reserveInventory(Long orderId, String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        if (!inventory.canReserve(quantity)) {
            throw new RuntimeException("Insufficient inventory for product: " + productId);
        }
        
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventoryRepository.save(inventory);
        
        log.info("Reserved {} units of product {} for order {}", quantity, productId, orderId);
    }
    
    @Transactional
    public void releaseInventory(Long orderId, String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        inventory.setReservedQuantity(Math.max(0, inventory.getReservedQuantity() - quantity));
        inventoryRepository.save(inventory);
        
        log.info("Released {} units of product {} for order {}", quantity, productId, orderId);
    }
    
    @Transactional
    public Inventory createInventory(String productId, Integer quantity) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);
        inventory.setReservedQuantity(0);
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Created inventory for product {}: {} units", productId, quantity);
        return savedInventory;
    }
    
    @Transactional(readOnly = true)
    public Inventory getInventoryByProductId(String productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
    }
    
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("inventoryService")
                .onMessage(ReserveInventoryCommand.class, this::handleReserveInventory)
                .onMessage(ReleaseInventoryCommand.class, this::handleReleaseInventory)
                .build();
    }
    
    private Message handleReserveInventory(CommandMessage<ReserveInventoryCommand> cm) {
        try {
            ReserveInventoryCommand command = cm.getCommand();
            reserveInventory(command.getOrderId(), command.getProductId(), command.getQuantity());
            return withSuccess();
        } catch (Exception e) {
            log.error("Failed to reserve inventory", e);
            return withFailure();
        }
    }
    
    private Message handleReleaseInventory(CommandMessage<ReleaseInventoryCommand> cm) {
        try {
            ReleaseInventoryCommand command = cm.getCommand();
            releaseInventory(command.getOrderId(), command.getProductId(), command.getQuantity());
            return withSuccess();
        } catch (Exception e) {
            log.error("Failed to release inventory", e);
            return withFailure();
        }
    }
}