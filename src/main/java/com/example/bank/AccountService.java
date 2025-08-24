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
    
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        // 입력 검증
        if (fromAccountId == null || toAccountId == null) {
            throw new IllegalArgumentException("Account IDs cannot be null");
        }
        
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        Account fromAccount = accounts.get(fromAccountId);
        Account toAccount = accounts.get(toAccountId);
        
        if (fromAccount == null) {
            throw new IllegalArgumentException("Sender account not found");
        }
        
        if (toAccount == null) {
            throw new IllegalArgumentException("Recipient account not found");
        }
        
        // Deadlock 방지를 위해 ID 순서로 동기화
        Account firstLock = fromAccountId < toAccountId ? fromAccount : toAccount;
        Account secondLock = fromAccountId < toAccountId ? toAccount : fromAccount;
        
        synchronized (firstLock) {
            synchronized (secondLock) {
                // 송금인 계좌에서 출금
                fromAccount.transferOut(amount, description, toAccountId);
                // 수취인 계좌에 입금
                toAccount.transferIn(amount, description, fromAccountId);
            }
        }
    }
}