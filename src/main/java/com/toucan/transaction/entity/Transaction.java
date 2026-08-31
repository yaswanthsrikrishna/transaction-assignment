package com.toucan.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @Column(name = "transaction_id", nullable = false, updatable = false, length = 64)
    private String transactionId;
    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false, length = 20)
    private TransactionStatus transactionStatus;

    protected Transaction() { }

    public Transaction(String transactionId, String customerId, BigDecimal amount, String currency,
                       TransactionType transactionType, TransactionStatus transactionStatus) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionId() { return transactionId; }
    public String getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public TransactionType getTransactionType() { return transactionType; }
    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public void updateStatus(TransactionStatus status) { this.transactionStatus = status; }
}