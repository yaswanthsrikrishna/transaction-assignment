-- Recreate the table so repeated H2 application contexts start cleanly.
DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
    transaction_id VARCHAR(64) NOT NULL,
    customer_id VARCHAR(64) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    transaction_status VARCHAR(20) NOT NULL,
    CONSTRAINT pk_transactions PRIMARY KEY (transaction_id)
);