package com.example.payment.command;

import io.eventuate.tram.commands.common.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentCommand implements Command {
    private Long orderId;
    private String customerId;
    private BigDecimal amount;
}