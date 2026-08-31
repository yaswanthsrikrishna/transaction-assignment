package com.toucan.transaction.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import com.toucan.transaction.dto.CreateTransactionRequest;
import com.toucan.transaction.dto.TransactionResponse;
import com.toucan.transaction.dto.UpdateStatusRequest;
import com.toucan.transaction.entity.Transaction;
import com.toucan.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Transactions", description = "Create and manage customer transactions")
public class TransactionRestController {
    private final TransactionService service;

    public TransactionRestController(TransactionService service) { this.service = service; }

    @PostMapping("/transactions")
    @Operation(summary = "Create a transaction")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Transaction to create",
            content = @Content(examples = @ExampleObject(value = "{\"transactionId\":\"tx-1001\",\"customerId\":\"customer-42\",\"amount\":125.5,\"currency\":\"USD\",\"transactionType\":\"PAYMENT\",\"transactionStatus\":\"PENDING\"}")))
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        Transaction transaction = service.create(request);
        return ResponseEntity.created(URI.create("/api/transactions/" + transaction.getTransactionId()))
                .body(TransactionResponse.from(transaction));
    }

    @GetMapping("/transactions/{transactionId}")
    @Operation(summary = "Get a transaction by ID")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction found", content = @Content(
                mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class),
                examples = @ExampleObject(name = "Sample transaction", value = "{\"transactionId\":\"tx-1001\",\"customerId\":\"customer-42\",\"amount\":125.50,\"currency\":\"USD\",\"transactionType\":\"PAYMENT\",\"transactionStatus\":\"PENDING\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
        })
        @Parameter(name = "transactionId", description = "Use tx-1001 to view seeded sample data", example = "tx-1001")
    public TransactionResponse get(@PathVariable String transactionId) {
        return TransactionResponse.from(service.get(transactionId));
    }

    @PatchMapping("/transactions/{transactionId}/status")
    @Operation(summary = "Update a transaction status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction status updated successfully", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = TransactionResponse.class),
                examples = @ExampleObject(value = "{\"transactionId\":\"tx-1001\",\"transactionStatus\":\"COMPLETED\",...}"))),
        @ApiResponse(responseCode = "400", description = "Invalid status transition or validation error"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<TransactionResponse> updateStatus(@PathVariable String transactionId,
                                                            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(
            TransactionResponse.from(service.updateStatus(transactionId, request.transactionStatus()))
        );
    }

    @GetMapping("/customers/{customerId}/transactions")
    @Operation(summary = "List transactions for a customer")
    @Parameter(name = "customerId", description = "Use customer-42 to view seeded sample data", example = "customer-42")
    public List<TransactionResponse> findByCustomer(@PathVariable String customerId) {
        return service.findByCustomer(customerId).stream().map(TransactionResponse::from).toList();
    }
}