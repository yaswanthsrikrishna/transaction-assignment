package com.toucan.transaction.service;

import com.toucan.transaction.dto.CreateTransactionRequest;
import com.toucan.transaction.entity.Transaction;
import com.toucan.transaction.entity.TransactionStatus;
import com.toucan.transaction.exception.DuplicateTransactionException;
import com.toucan.transaction.exception.InvalidTransactionException;
import com.toucan.transaction.exception.TransactionNotFoundException;
import com.toucan.transaction.repository.TransactionRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) { this.repository = repository; }

    @Transactional
    public Transaction create(CreateTransactionRequest request) {
        logger.info("Creating new transaction: id={}, customerId={}, amount={} {}", 
            request.transactionId(), request.customerId(), request.amount(), request.currency());
        
        if (request.transactionStatus() != TransactionStatus.PENDING) {
            logger.warn("Invalid initial status for transaction {}: {}", 
                request.transactionId(), request.transactionStatus());
            throw new InvalidTransactionException("New transactions must have PENDING status");
        }
        if (repository.existsById(request.transactionId())) {
            logger.warn("Duplicate transaction ID attempted: {}", request.transactionId());
            throw new DuplicateTransactionException(request.transactionId());
        }
        Transaction transaction = repository.save(new Transaction(request.transactionId(), request.customerId(), request.amount(),
                request.currency(), request.transactionType(), request.transactionStatus()));
        logger.info("Transaction created successfully: id={}", request.transactionId());
        return transaction;
    }

    @Transactional(readOnly = true)
    public Transaction get(String transactionId) {
        return repository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    @Transactional
    public Transaction updateStatus(String transactionId, TransactionStatus requestedStatus) {
        logger.debug("Status update requested: id={}, newStatus={}", transactionId, requestedStatus);
        
        Transaction transaction = get(transactionId);
        TransactionStatus currentStatus = transaction.getTransactionStatus();

        if (currentStatus == TransactionStatus.PENDING) {
            if (requestedStatus == TransactionStatus.COMPLETED || requestedStatus == TransactionStatus.FAILED) {
                transaction.updateStatus(requestedStatus);
                logger.info("Status updated successfully: id={}, from=PENDING, to={}", 
                    transactionId, requestedStatus);
                return transaction;
            }
            logger.warn("Invalid status transition: id={}, from=PENDING, to={}", 
                transactionId, requestedStatus);
            throw new InvalidTransactionException("Pending transactions can only transition to COMPLETED or FAILED");
        }

        logger.warn("Attempted status update on terminal state: id={}, currentStatus={}", 
            transactionId, currentStatus);
        throw new InvalidTransactionException("Transaction is already in a terminal state: " + currentStatus);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByCustomer(String customerId) {
        return repository.findAllByCustomerIdOrderByTransactionId(customerId);
    }
}