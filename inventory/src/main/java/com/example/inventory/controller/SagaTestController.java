package com.example.inventory.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/saga")
@RequiredArgsConstructor
@Slf4j
public class SagaTestController {
    
    private final InventoryService inventoryService;
    
    @GetMapping("/test/inventory")
    public ResponseEntity<String> test2() {
        try {
            inventoryService.createInventory("1", 100);
            return ResponseEntity.ok("Inventory created for product: " + "1");
        } catch (Exception e) {
            log.error("Failed to create inventory", e);
            return ResponseEntity.badRequest().body("Failed to create inventory: " + e.getMessage());
        }
    }
    
    @PostMapping("/inventory")
    public ResponseEntity<String> createInventory(@RequestBody CreateInventoryRequest request) {
        try {
            inventoryService.createInventory(request.getProductId(), request.getQuantity());
            return ResponseEntity.ok("Inventory created for product: " + request.getProductId());
        } catch (Exception e) {
            log.error("Failed to create inventory", e);
            return ResponseEntity.badRequest().body("Failed to create inventory: " + e.getMessage());
        }
    }
    
    public static class CreateOrderRequest {
        private String customerId;
        private String productId;
        private Integer quantity;
        private BigDecimal amount;
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
    
    public static class CreateInventoryRequest {
        private String productId;
        private Integer quantity;
        
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
}