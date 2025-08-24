package com.example.bank;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final ConcurrentHashMap<Long, Account> accounts = new ConcurrentHashMap<>();
    
    public Account createAccount(String accountHolder, BigDecimal initialDeposit) {
        if (accountHolder == null || accountHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name is required");
        }
        
        if (initialDeposit != null && initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }
        
        Account account = new Account(accountHolder.trim(), initialDeposit);
        accounts.put(account.getId(), account);
        return account;
    }
    
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }
    
    public Optional<Account> getAccount(Long accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
    
    public Account deposit(Long accountId, BigDecimal amount, String description) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        account.deposit(amount, description);
        return account;
    }
    
    public Account withdraw(Long accountId, BigDecimal amount, String description) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        account.withdraw(amount, description);
        return account;
    }
    
    public List<Transaction> getTransactionHistory(Long accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        return account.getTransactions();
    }
}