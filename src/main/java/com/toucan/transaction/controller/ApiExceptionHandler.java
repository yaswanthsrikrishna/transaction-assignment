package com.toucan.transaction.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import com.toucan.transaction.exception.DuplicateTransactionException;
import com.toucan.transaction.exception.InvalidTransactionException;
import com.toucan.transaction.exception.TransactionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(TransactionNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(TransactionNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({DuplicateTransactionException.class, InvalidTransactionException.class,
            MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<Map<String, String>> badRequest(Exception exception) {
        String message = exception instanceof MethodArgumentNotValidException
                ? "Request validation failed" : exception.getMessage();
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> invalidJson(HttpMessageNotReadableException exception) {
        String message = "Invalid JSON format or missing required fields";
        if (exception.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) exception.getCause();
            message = String.format("Invalid value '%s' for field: %s", 
                ife.getValue(), 
                ife.getPath().isEmpty() ? "unknown" : ife.getPath().get(0).getFieldName());
        }
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> genericError(Exception exception) {
        exception.printStackTrace();
        return error(HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred. Please contact support.");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}