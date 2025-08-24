package com.example.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Transaction {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    
    public enum TransactionType {
        DEPOSIT("입금"),
        WITHDRAWAL("출금");
        
        private final String displayName;
        
        TransactionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private final Long id;
    private final Long accountId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime timestamp;
    
    public Transaction(Long accountId, TransactionType type, BigDecimal amount, String description) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public Long getAccountId() {
        return accountId;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}