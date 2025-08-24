package com.example.bank;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    
    public List<Account> getOtherAccounts(Long excludeAccountId) {
        return accounts.values().stream()
                .filter(account -> !account.getId().equals(excludeAccountId))
                .collect(Collectors.toList());
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
    
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        // 기본 검증
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("송금 금액은 양수여야 합니다");
        }
        
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("본인 계좌로는 송금할 수 없습니다");
        }
        
        Account fromAccount = accounts.get(fromAccountId);
        Account toAccount = accounts.get(toAccountId);
        
        if (fromAccount == null) {
            throw new IllegalArgumentException("송금인 계좌를 찾을 수 없습니다");
        }
        
        if (toAccount == null) {
            throw new IllegalArgumentException("수취인 계좌를 찾을 수 없습니다");
        }
        
        // 동시성 처리: 계좌 ID 순서로 락을 획득하여 deadlock 방지
        Account firstAccount, secondAccount;
        if (fromAccountId < toAccountId) {
            firstAccount = fromAccount;
            secondAccount = toAccount;
        } else {
            firstAccount = toAccount;
            secondAccount = fromAccount;
        }
        
        synchronized (firstAccount) {
            synchronized (secondAccount) {
                // 송금인 잔액 확인
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: ₩" + fromAccount.getBalance());
                }
                
                // 송금 처리
                fromAccount.transferOut(amount, description, toAccountId);
                toAccount.transferIn(amount, description, fromAccountId);
            }
        }
    }
    
    public List<Transaction> getTransactionHistory(Long accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        return account.getTransactions();
    }
}