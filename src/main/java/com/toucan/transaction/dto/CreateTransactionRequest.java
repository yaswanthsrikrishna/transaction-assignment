package com.toucan.transaction.dto;

import com.toucan.transaction.entity.TransactionStatus;
import com.toucan.transaction.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank @Size(max = 64) String transactionId,
        @NotBlank @Size(max = 64) String customerId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
        @NotNull TransactionType transactionType,
        @NotNull TransactionStatus transactionStatus) { }