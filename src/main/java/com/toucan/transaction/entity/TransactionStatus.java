package com.toucan.transaction.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionStatus {
    PENDING, COMPLETED, FAILED;

    @JsonCreator
    public static TransactionStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        String normalizedValue = value.trim().toUpperCase();
        if (normalizedValue.equals("COMPLETE")) {
            normalizedValue = "COMPLETED";
        }
        return valueOf(normalizedValue);
    }
}