package com.toucan.transaction.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTests {
    @Autowired MockMvc mockMvc;

    @Test
    void createsAndGetsTransaction() throws Exception {
        create("tx-101", "customer-1");
        mockMvc.perform(get("/api/transactions/tx-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("tx-101"))
                .andExpect(jsonPath("$.amount").value(19.99));
    }

    @Test
    void updatesPendingStatus() throws Exception {
        create("tx-102", "customer-2");
        mockMvc.perform(patch("/api/transactions/tx-102/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionStatus\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionStatus").value("COMPLETED"));
    }

    @Test
    void listsTransactionsForCustomer() throws Exception {
        create("tx-103", "customer-3");
        create("tx-104", "customer-3");
        mockMvc.perform(get("/api/customers/customer-3/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].transactionId").value("tx-103"));
    }

    @Test
    void rejectsInvalidInitialStatus() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\":\"tx-105\",\"customerId\":\"customer-4\","
                                + "\"amount\":10,\"currency\":\"USD\",\"transactionType\":\"PAYMENT\","
                                + "\"transactionStatus\":\"COMPLETED\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsStatusUpdateAfterCompletion() throws Exception {
        create("tx-106", "customer-5");
        update("tx-106", "COMPLETED");
        mockMvc.perform(patch("/api/transactions/tx-106/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionStatus\":\"FAILED\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptsLowercaseStatus() throws Exception {
        create("tx-107", "customer-6");
        mockMvc.perform(patch("/api/transactions/tx-107/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionStatus\":\"complete\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionStatus").value("COMPLETED"));
    }

    @Test
    void exposesOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Customer Transactions API"));
    }

    private void create(String transactionId, String customerId) throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\":\"" + transactionId + "\",\"customerId\":\""
                                + customerId + "\",\"amount\":19.99,\"currency\":\"USD\","
                                + "\"transactionType\":\"PAYMENT\",\"transactionStatus\":\"PENDING\"}"))
                .andExpect(status().isCreated());
    }

    private void update(String transactionId, String status) throws Exception {
        mockMvc.perform(patch("/api/transactions/" + transactionId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionStatus\":\"" + status + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsDuplicateTransactionId() throws Exception {
        create("tx-duplicate", "customer-10");
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\":\"tx-duplicate\",\"customerId\":\"customer-11\","
                                + "\"amount\":50.00,\"currency\":\"USD\",\"transactionType\":\"PAYMENT\","
                                + "\"transactionStatus\":\"PENDING\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(containsString("tx-duplicate")));
    }

    @Test
    void returns404ForMissingTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/nonexistent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("nonexistent-id")));
    }

    @Test
    void returnsEmptyListForCustomerWithoutTransactions() throws Exception {
        mockMvc.perform(get("/api/customers/unknown-customer-xyz/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void rejectsInvalidCurrencyFormat() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\":\"tx-badcur\",\"customerId\":\"customer-12\","
                                + "\"amount\":50.00,\"currency\":\"INVALID\",\"transactionType\":\"PAYMENT\","
                                + "\"transactionStatus\":\"PENDING\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAmountBelowMinimum() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\":\"tx-lowamt\",\"customerId\":\"customer-13\","
                                + "\"amount\":0.00,\"currency\":\"USD\",\"transactionType\":\"PAYMENT\","
                                + "\"transactionStatus\":\"PENDING\"}"))
                .andExpect(status().isBadRequest());
    }
}