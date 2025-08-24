package com.example.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void createAccount_Success() {
        BigDecimal initialDeposit = new BigDecimal("1000");
        Account account = accountService.createAccount("홍길동", initialDeposit);
        
        assertNotNull(account);
        assertEquals("홍길동", account.getAccountHolder());
        assertEquals(initialDeposit, account.getBalance());
        assertNotNull(account.getId());
    }

    @Test
    void createAccount_WithoutInitialDeposit() {
        Account account = accountService.createAccount("김철수", null);
        
        assertNotNull(account);
        assertEquals("김철수", account.getAccountHolder());
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void createAccount_EmptyName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount("", new BigDecimal("1000"));
        });
    }

    @Test
    void deposit_Success() {
        Account account = accountService.createAccount("홍길동", new BigDecimal("1000"));
        BigDecimal depositAmount = new BigDecimal("500");
        
        Account updatedAccount = accountService.deposit(account.getId(), depositAmount, "테스트 입금");
        
        assertEquals(new BigDecimal("1500"), updatedAccount.getBalance());
    }

    @Test
    void withdraw_Success() {
        Account account = accountService.createAccount("홍길동", new BigDecimal("1000"));
        BigDecimal withdrawAmount = new BigDecimal("300");
        
        Account updatedAccount = accountService.withdraw(account.getId(), withdrawAmount, "테스트 출금");
        
        assertEquals(new BigDecimal("700"), updatedAccount.getBalance());
    }

    @Test
    void withdraw_InsufficientBalance_ThrowsException() {
        Account account = accountService.createAccount("홍길동", new BigDecimal("100"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdraw(account.getId(), new BigDecimal("200"), "테스트 출금");
        });
    }

    @Test
    void getAllAccounts() {
        accountService.createAccount("홍길동", new BigDecimal("1000"));
        accountService.createAccount("김철수", new BigDecimal("2000"));
        
        List<Account> accounts = accountService.getAllAccounts();
        
        assertEquals(2, accounts.size());
    }

    @Test
    void getAccount_Exists() {
        Account account = accountService.createAccount("홍길동", new BigDecimal("1000"));
        
        Optional<Account> foundAccount = accountService.getAccount(account.getId());
        
        assertTrue(foundAccount.isPresent());
        assertEquals("홍길동", foundAccount.get().getAccountHolder());
    }

    @Test
    void getAccount_NotExists() {
        Optional<Account> foundAccount = accountService.getAccount(999L);
        
        assertFalse(foundAccount.isPresent());
    }
}