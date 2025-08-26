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
    
    /**
     * 계좌 간 송금을 처리한다.
     * 데드락 방지를 위해 계좌 ID 순서로 락을 획득한다.
     * 
     * @param fromAccountId 송금하는 계좌 ID
     * @param toAccountId 송금받는 계좌 ID
     * @param amount 송금액 (양수여야 함)
     * @param description 송금 설명 (옵션)
     * @return 송금한 계좌 객체
     * @throws IllegalArgumentException 계좌을 찾을 수 없거나 송금 조건이 맞지 않는 경우
     */
    public Account transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        // 입력 값 검증
        if (fromAccountId == null || toAccountId == null) {
            throw new IllegalArgumentException("Account IDs cannot be null");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        // 본인 계좌로 송금 방지
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        // 계좌 존재 확인
        Account fromAccount = accounts.get(fromAccountId);
        Account toAccount = accounts.get(toAccountId);
        
        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account not found: " + fromAccountId);
        }
        
        if (toAccount == null) {
            throw new IllegalArgumentException("Target account not found: " + toAccountId);
        }
        
        // 데드락 방지를 위해 ID 순서로 락 획득
        Account firstLock = fromAccountId < toAccountId ? fromAccount : toAccount;
        Account secondLock = fromAccountId < toAccountId ? toAccount : fromAccount;
        
        synchronized (firstLock) {
            synchronized (secondLock) {
                // 송금 처리
                fromAccount.transferOut(amount, description, toAccountId);
                toAccount.transferIn(amount, description, fromAccountId);
            }
        }
        
        return fromAccount;
    }
}