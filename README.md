# Transaction Starter Project

This is the starter project for the Customer Transactions exercise.

# Transaction Starter Project

A Spring Boot REST API for creating and managing customer transactions. It uses Java 17, Spring Web, Spring Data JPA, Bean Validation, Springdoc OpenAPI, and an embedded H2 database.

## Requirements

- Java 17 or newer
- No Maven installation is required; the Maven wrapper is included

## Run and test

Linux / macOS:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd clean test
mvnw.cmd spring-boot:run
```

The application starts on `http://localhost:8080`.

## API

All endpoints return JSON. Enum values are uppercase.

### Create a transaction

`POST /api/transactions`

```json
{
	"transactionId": "tx-1001",
	"customerId": "customer-42",
	"amount": 125.50,
	"currency": "USD",
	"transactionType": "PAYMENT",
	"transactionStatus": "PENDING"
}
```

Returns `201 Created`, the transaction JSON, and a `Location` header. Transaction IDs must be unique.

### Get a transaction

`GET /api/transactions/{transactionId}`

Returns `200 OK`, or `404 Not Found` if the transaction does not exist.

### Update transaction status

`PATCH /api/transactions/{transactionId}/status`

```json
{ "transactionStatus": "COMPLETED" }
```

The status value is case-insensitive, so `complete` is accepted and returned as `COMPLETED`. Any supported status (`PENDING`, `COMPLETED`, or `FAILED`) can replace the existing status. Invalid status names return `400 Bad Request`.

### Get customer transactions

`GET /api/customers/{customerId}/transactions`

Returns `200 OK` and an array ordered by transaction ID. The array is empty when the customer has no transactions.

## Transaction model

Every transaction contains:

- `transactionId`: required, non-blank, maximum 64 characters
- `customerId`: required, non-blank, maximum 64 characters
- `amount`: required and at least `0.01`
- `currency`: required, exactly three uppercase letters, for example `USD`
- `transactionType`: `PAYMENT`, `REFUND`, or `TRANSFER`
- `transactionStatus`: `PENDING`, `COMPLETED`, or `FAILED`

Business rules beyond annotation validation:

1. New transactions must start with `PENDING` status.
2. A `PENDING` transaction may transition once to `COMPLETED` or `FAILED`.
3. Transaction IDs are unique.

Errors use this shape:

```json
{
	"error": "Bad Request",
	"message": "Request validation failed"
}
```

## Project structure

- `controller/TransactionRestController.java`: HTTP endpoints
- `controller/ApiExceptionHandler.java`: consistent error responses
- `config/OpenApiConfiguration.java`: Swagger metadata
- `dto/`: request and response objects
- `entity/Transaction.java`: JPA entity
- `entity/TransactionStatus.java`: allowed transaction statuses
- `entity/TransactionType.java`: allowed transaction types
- `repository/TransactionRepository.java`: database access
- `service/TransactionService.java`: business rules
- `exception/`: domain exceptions

The SQL schema in `schema.sql` creates the `transactions` table with these columns: `transaction_id`, `customer_id`, `amount`, `currency`, `transaction_type`, and `transaction_status`. JPA validates this schema at startup, while `data.sql` inserts sample rows.

The H2 database is in-memory and is recreated on application restart. The H2 console is available at `/h2-console` while the application is running.

## Swagger UI

OpenAPI documentation is available at `/swagger-ui.html`. The raw specification is available at `/v3/api-docs`.

## Sample data

The application loads `data.sql` automatically at startup. It creates these records in the H2 `transactions` table:

- `tx-1001` for `customer-42`: `125.50 USD`, `PAYMENT`, `PENDING`
- `tx-1002` for `customer-42`: `75.00 EUR`, `REFUND`, `COMPLETED`
- `tx-1003` for `customer-99`: `250.00 GBP`, `TRANSFER`, `FAILED`

## Tests

The integration tests cover creation and retrieval, status updates, customer filtering, invalid initial status, terminal-state protection, and OpenAPI availability. Run the complete suite with `mvnw.cmd clean test` on Windows or `./mvnw clean test` on Linux/macOS.

## AI usage disclosure

GitHub Copilot was used to help organize packages, add OpenAPI configuration, create the SQL schema and sample data, improve documentation, and review test coverage. The implementation was checked by reviewing the generated code, running the Maven test suite, and correcting package and H2 initialization issues found during validation.

