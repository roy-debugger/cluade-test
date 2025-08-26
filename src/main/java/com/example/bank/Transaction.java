package com.example.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Transaction {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    
    public enum TransactionType {
        DEPOSIT("입금"),
        WITHDRAWAL("출금"),
        TRANSFER_OUT("송금"),
        TRANSFER_IN("입금받음");
        
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
    private final Long targetAccountId;
    
    /**
     * 새 거래 내역을 생성한다. (일반 입출금용)
     * 
     * @param accountId 계좌 ID
     * @param type 거래 종류 (DEPOSIT, WITHDRAWAL)
     * @param amount 거래 금액
     * @param description 거래 설명
     */
    public Transaction(Long accountId, TransactionType type, BigDecimal amount, String description) {
        this(accountId, type, amount, description, null);
    }
    
    /**
     * 새 거래 내역을 생성한다. (송금/입금받음용)
     * 
     * @param accountId 계좌 ID
     * @param type 거래 종류 (DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT)
     * @param amount 거래 금액
     * @param description 거래 설명
     * @param targetAccountId 상대방 계좌 ID (송금/입금받음 시 필수)
     */
    public Transaction(Long accountId, TransactionType type, BigDecimal amount, String description, Long targetAccountId) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.targetAccountId = targetAccountId;
    }
    
    /**
     * 거래 ID를 반환한다.
     * 
     * @return 거래의 고유 ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * 계좌 ID를 반환한다.
     * 
     * @return 거래가 발생한 계좌의 ID
     */
    public Long getAccountId() {
        return accountId;
    }
    
    /**
     * 거래 종류를 반환한다.
     * 
     * @return 거래의 종류 (DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT)
     */
    public TransactionType getType() {
        return type;
    }
    
    /**
     * 거래 금액을 반환한다.
     * 
     * @return 거래 금액
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * 거래 설명을 반환한다.
     * 
     * @return 거래 설명
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 거래 발생 시간을 반환한다.
     * 
     * @return 거래가 발생한 시간
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * 송금/입금받음 시 상대방 계좌 ID를 반환한다.
     * 
     * @return 상대방 계좌 ID (송금/입금받음이 아니면 null)
     */
    public Long getTargetAccountId() {
        return targetAccountId;
    }
}