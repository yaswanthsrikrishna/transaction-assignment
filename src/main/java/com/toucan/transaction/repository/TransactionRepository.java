package com.toucan.transaction.repository;

import com.toucan.transaction.entity.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByCustomerIdOrderByTransactionId(String customerId);
}