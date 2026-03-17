package com.example.order.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.domain.Order;
import com.example.order.saga.OrderSagaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/saga")
@RequiredArgsConstructor
@Slf4j
public class SagaTestController {
    
    private final OrderSagaService orderSagaService;
    @GetMapping("/test/order")
    public ResponseEntity<OrderResponse> test() {
        try {
            Order order = orderSagaService.createOrderWithSaga(
                "test",
                "1",
                5,
                new BigDecimal(5000)
            );
            
            return ResponseEntity.ok(new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus().toString(),
                "Order created and saga started"
            ));
        } catch (Exception e) {
            log.error("Failed to create order with saga", e);
            return ResponseEntity.badRequest().body(new OrderResponse(
                null, null, null, null, null, "FAILED", e.getMessage()
            ));
        }
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Order order = orderSagaService.createOrderWithSaga(
                request.getCustomerId(),
                request.getProductId(),
                request.getQuantity(),
                request.getAmount()
            );
            
            return ResponseEntity.ok(new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus().toString(),
                "Order created and saga started"
            ));
        } catch (Exception e) {
            log.error("Failed to create order with saga", e);
            return ResponseEntity.badRequest().body(new OrderResponse(
                null, null, null, null, null, "FAILED", e.getMessage()
            ));
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
    
    public static class OrderResponse {
        private Long orderId;
        private String customerId;
        private String productId;
        private Integer quantity;
        private BigDecimal amount;
        private String status;
        private String message;
        
        public OrderResponse(Long orderId, String customerId, String productId, 
                           Integer quantity, BigDecimal amount, String status, String message) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.productId = productId;
            this.quantity = quantity;
            this.amount = amount;
            this.status = status;
            this.message = message;
        }
        
        public Long getOrderId() { return orderId; }
        public String getCustomerId() { return customerId; }
        public String getProductId() { return productId; }
        public Integer getQuantity() { return quantity; }
        public BigDecimal getAmount() { return amount; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }
}