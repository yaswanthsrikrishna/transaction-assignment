-- Sample rows are loaded automatically after schema.sql.
INSERT INTO transactions (transaction_id, customer_id, amount, currency, transaction_type, transaction_status)
VALUES ('tx-1001', 'customer-42', 125.50, 'USD', 'PAYMENT', 'PENDING');

INSERT INTO transactions (transaction_id, customer_id, amount, currency, transaction_type, transaction_status)
VALUES ('tx-1002', 'customer-42', 75.00, 'EUR', 'REFUND', 'COMPLETED');

INSERT INTO transactions (transaction_id, customer_id, amount, currency, transaction_type, transaction_status)
VALUES ('tx-1003', 'customer-99', 250.00, 'GBP', 'TRANSFER', 'FAILED');