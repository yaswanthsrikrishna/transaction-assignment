package com.toucan.transaction.dto;

import com.toucan.transaction.entity.Transaction;
import com.toucan.transaction.entity.TransactionStatus;
import com.toucan.transaction.entity.TransactionType;

import java.math.BigDecimal;

public record TransactionResponse(String transactionId, String customerId, BigDecimal amount,
                                  String currency, TransactionType transactionType,
                                  TransactionStatus transactionStatus) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(transaction.getTransactionId(), transaction.getCustomerId(),
                transaction.getAmount(), transaction.getCurrency(), transaction.getTransactionType(),
                transaction.getTransactionStatus());
    }
}