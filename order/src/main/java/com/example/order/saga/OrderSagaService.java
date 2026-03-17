package com.example.order.saga;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order.domain.Order;
import com.example.order.service.OrderService;

import io.eventuate.tram.sagas.orchestration.SagaInstance;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaService {
    
    private final OrderService orderService;
    private final SagaInstanceFactory sagaInstanceFactory;
    private final OrderProcessingSaga orderProcessingSaga;
    
    @Transactional
    public Order createOrderWithSaga(String customerId, String productId, Integer quantity, BigDecimal amount) {
        // 1. 주문 생성
        Order order = orderService.createOrder(customerId, productId, quantity, amount);
        
        // 2. Saga 데이터 준비
        OrderProcessingSaga.OrderProcessingSagaData sagaData = 
            new OrderProcessingSaga.OrderProcessingSagaData(
                order.getId(),
                customerId,
                productId,
                quantity,
                amount
            );
        
        // 3. Saga 시작
        SagaInstance si=sagaInstanceFactory.create(orderProcessingSaga, sagaData);
        // si.addDestinationsAndResources(Collections.emptySet());
        
        log.info("Started order processing saga for order: {}", order.getId());
        
        return order;
    }
}