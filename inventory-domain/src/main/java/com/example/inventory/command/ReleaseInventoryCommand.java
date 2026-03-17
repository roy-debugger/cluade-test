package com.example.inventory.command;

import io.eventuate.tram.commands.common.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseInventoryCommand implements Command {
    private Long orderId;
    private String productId;
    private Integer quantity;
}