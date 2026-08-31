package com.toucan.transaction.exception;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) { super(message); }
}