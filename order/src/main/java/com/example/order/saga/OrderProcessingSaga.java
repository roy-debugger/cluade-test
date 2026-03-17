package com.example.order.saga;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.inventory.command.ReleaseInventoryCommand;
import com.example.inventory.command.ReserveInventoryCommand;
import com.example.order.command.ApproveOrderCommand;
import com.example.order.command.RejectOrderCommand;
import com.example.order.domain.Order.OrderStatus;
import com.example.order.service.OrderService;
import com.example.payment.command.CancelPaymentCommand;
import com.example.payment.command.ProcessPaymentCommand;

import io.eventuate.tram.commands.common.Failure;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import com.example.order.service.OrderService;


@Component
@Slf4j
public class OrderProcessingSaga implements SimpleSaga<OrderProcessingSaga.OrderProcessingSagaData> {

    @Autowired
    private OrderService orderService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProcessingSagaData {
        private Long orderId;
        private String customerId;
        private String productId;
        private Integer quantity;
        private BigDecimal amount;
    }

    private SagaDefinition<OrderProcessingSagaData> sagaDefinition = 
        step()
            .invokeLocal(this::doNothing)
            .withCompensation(this::rejectOrderLocal)
        .step()
            .invokeParticipant(this::reserveInventory)
            .onReply(Failure.class, this::handleInventoryFailure)
            .withCompensation(this::releaseInventory)
        .step()
            .invokeParticipant(this::processPayment)
            .onReply(Failure.class, this::handlePaymentFailure)
            .withCompensation(this::cancelPayment)
        .step()
            .invokeParticipant(this::approveOrder)
            .onReply(Failure.class, this::handleOrderFailure)
        .build();

    @Override
    public SagaDefinition<OrderProcessingSagaData> getSagaDefinition() {
        return sagaDefinition;
    }

    private CommandWithDestination reserveInventory(OrderProcessingSagaData data) {
        return send(new ReserveInventoryCommand(data.getOrderId(), data.getProductId(), data.getQuantity()))
                .to("inventoryService")
                .build(); 
    }

    private CommandWithDestination releaseInventory(OrderProcessingSagaData data) {
        return send(new ReleaseInventoryCommand(data.getOrderId(), data.getProductId(), data.getQuantity()))
                .to("inventoryService")
                .build();
    }

    private CommandWithDestination processPayment(OrderProcessingSagaData data) {
        return send(new ProcessPaymentCommand(data.getOrderId(), data.getCustomerId(), data.getAmount()))
                .to("paymentService")
                .build();
    }

    private CommandWithDestination cancelPayment(OrderProcessingSagaData data) {
        return send(new CancelPaymentCommand(data.getOrderId()))
                .to("paymentService")
                .build();
    }

    private CommandWithDestination approveOrder(OrderProcessingSagaData data) {
        return send(new ApproveOrderCommand(data.getOrderId()))
                .to("orderService")
                .build();
    }
    
    // // 실패 시 주문 거절을 위한 보상 트랜잭션
    // private CommandWithDestination rejectOrder(OrderProcessingSagaData data) {
    //     log.error("order cancel!!!");
    //     return send(new RejectOrderCommand(data.getOrderId()))
    //             .to("orderService")
    //             .build();
    // }

    // 실패 시 주문 거절을 위한 보상 트랜잭션
    private void rejectOrderLocal(OrderProcessingSagaData data) {
        orderService.rejectOrder(data.getOrderId());
    }

    // 핸들러 메서드
    private void handleInventoryFailure(OrderProcessingSagaData data, Failure failure) {
        log.error("inventory reservation error!!");
    }

    private void handlePaymentFailure(OrderProcessingSagaData data, Failure failure) {
        log.error("payment error!!");
    }

    private void handleOrderFailure(OrderProcessingSagaData data, Failure failure) {
        log.error("order error!!");
    }

    private void doNothing(OrderProcessingSagaData data) {

    }


    @Override
    public void onSagaCompletedSuccessfully(String sagaId, OrderProcessingSagaData data) {
        log.info("saga 성공");
    }

    @Override
    public void onSagaRolledBack(String sagaId, OrderProcessingSagaData data) {
        log.info("saga 롤백");
    }
    @Override
    public void onSagaFailed(String sagaId, OrderProcessingSagaData data) {
        log.info("saga 실패");
    }
}