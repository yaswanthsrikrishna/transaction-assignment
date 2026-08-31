package com.toucan.transaction.exception;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String transactionId) { super("Transaction already exists: " + transactionId); }
}