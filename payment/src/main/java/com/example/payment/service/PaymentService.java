package com.example.payment.service;

import com.example.payment.command.CancelPaymentCommand;
import com.example.payment.command.ProcessPaymentCommand;
import com.example.payment.domain.Payment;
import com.example.payment.repository.PaymentRepository;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    @Transactional
    public Payment processPayment(Long orderId, String customerId, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setCustomerId(customerId);
        payment.setAmount(amount);
        
        // 간단한 결제 로직 시뮬레이션
        boolean paymentSuccess = simulatePaymentProcessing(amount);
        
        if (paymentSuccess) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Processed payment for order {}: {}", orderId, savedPayment.getStatus());
        
        if (!paymentSuccess) {
            throw new RuntimeException("Payment failed for order: " + orderId);
        }
        
        return savedPayment;
    }
    
    @Transactional
    public void cancelPayment(Long orderId) {
        // 실제로는 orderId로 결제를 찾아야 하지만, 예시를 위해 단순화
        log.info("Cancelled payment for order: {}", orderId);
    }
    
    private boolean simulatePaymentProcessing(BigDecimal amount) {
        // 결제 금액이 1000 이상이면 10% 확률로 실패
        if (amount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return Math.random() > 0.1;
        }
        return true;
    }
    
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("paymentService")
                .onMessage(ProcessPaymentCommand.class, this::handleProcessPayment)
                .onMessage(CancelPaymentCommand.class, this::handleCancelPayment)
                .build();
    }
    
    private Message handleProcessPayment(CommandMessage<ProcessPaymentCommand> cm) {
        try {
            ProcessPaymentCommand command = cm.getCommand();
            processPayment(command.getOrderId(), command.getCustomerId(), command.getAmount());
            return withSuccess();
        } catch (Exception e) {
            log.error("Failed to process payment", e);
            return withFailure();
        }
    }
    
    private Message handleCancelPayment(CommandMessage<CancelPaymentCommand> cm) {
        try {
            CancelPaymentCommand command = cm.getCommand();
            cancelPayment(command.getOrderId());
            return withSuccess();
        } catch (Exception e) {
            log.error("Failed to cancel payment", e);
            return withFailure();
        }
    }
}