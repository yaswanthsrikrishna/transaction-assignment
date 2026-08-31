package com.toucan.transaction.dto;

import com.toucan.transaction.entity.TransactionStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull TransactionStatus transactionStatus) { }