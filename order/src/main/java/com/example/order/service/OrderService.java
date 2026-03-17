package com.example.order.service;

import com.example.order.command.ApproveOrderCommand;
import com.example.order.command.RejectOrderCommand;
import com.example.order.domain.Order;
import com.example.order.repository.OrderRepository;
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
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    @Transactional
    public Order createOrder(String customerId, String productId, Integer quantity, java.math.BigDecimal amount) {
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setAmount(amount);
        order.setStatus(Order.OrderStatus.PENDING);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Created order: {}", savedOrder.getId());
        return savedOrder;
    }
    
    @Transactional
    public void approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

            
        // throw new RuntimeException("Order error!!! " + orderId);
        
        order.setStatus(Order.OrderStatus.APPROVED);
        orderRepository.save(order);
        log.info("Approved order: {}", orderId);
    }
    
    @Transactional
    public void rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        order.setStatus(Order.OrderStatus.REJECTED);
        orderRepository.save(order);
        log.info("Rejected order: {}", orderId);
    }
    
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("orderService")
                .onMessage(ApproveOrderCommand.class, this::handleApproveOrder)
                .onMessage(RejectOrderCommand.class, this::handleRejectOrder)
                .build();
    }
    
    private Message handleApproveOrder(CommandMessage<ApproveOrderCommand> cm) {
        try {
            ApproveOrderCommand command = cm.getCommand();
            approveOrder(command.getOrderId());
            return withSuccess();
        } catch (Exception e) {
            log.error("Failed to approve order", e);
            return withFailure();
        }
    }
    
    private Message handleRejectOrder(CommandMessage<RejectOrderCommand> cm) {
        try {
            RejectOrderCommand command = cm.getCommand();
            rejectOrder(command.getOrderId());
            return withSuccess();
        } catch (Exception e) {
            log.error("Failed to reject order", e);
            return withFailure();
        }
    }
}