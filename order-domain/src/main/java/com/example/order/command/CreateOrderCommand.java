package com.example.order.command;

import io.eventuate.tram.commands.common.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand implements Command {
    private String customerId;
    private String productId;
    private Integer quantity;
    private BigDecimal amount;
}