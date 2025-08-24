package com.example.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Account {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    
    private final Long id;
    private final String accountHolder;
    private BigDecimal balance;
    private final LocalDateTime createdAt;
    private final List<Transaction> transactions;
    
    public Account(String accountHolder, BigDecimal initialDeposit) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.accountHolder = accountHolder;
        this.balance = initialDeposit != null ? initialDeposit : BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
        
        if (initialDeposit != null && initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            this.transactions.add(new Transaction(
                this.id,
                Transaction.TransactionType.DEPOSIT,
                initialDeposit,
                "Initial deposit"
            ));
        }
    }
    
    public synchronized void deposit(BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        this.balance = this.balance.add(amount);
        this.transactions.add(new Transaction(
            this.id,
            Transaction.TransactionType.DEPOSIT,
            amount,
            description != null ? description : "Deposit"
        ));
    }
    
    public synchronized void withdraw(BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        this.balance = this.balance.subtract(amount);
        this.transactions.add(new Transaction(
            this.id,
            Transaction.TransactionType.WITHDRAWAL,
            amount,
            description != null ? description : "Withdrawal"
        ));
    }
    
    public Long getId() {
        return id;
    }
    
    public String getAccountHolder() {
        return accountHolder;
    }
    
    public synchronized BigDecimal getBalance() {
        return balance;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public synchronized List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public synchronized void transferOut(BigDecimal amount, String description, Long targetAccountId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        this.balance = this.balance.subtract(amount);
        this.transactions.add(new Transaction(
            this.id,
            Transaction.TransactionType.TRANSFER_OUT,
            amount,
            description != null ? description : "Transfer",
            targetAccountId
        ));
    }
    
    public synchronized void transferIn(BigDecimal amount, String description, Long sourceAccountId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        this.balance = this.balance.add(amount);
        this.transactions.add(new Transaction(
            this.id,
            Transaction.TransactionType.TRANSFER_IN,
            amount,
            description != null ? description : "Transfer received",
            sourceAccountId
        ));
    }
}