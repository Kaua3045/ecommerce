package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.infrastructure.utils.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(final DomainException exception) {
        return ResponseEntity.unprocessableEntity().body(ApiError.from(exception));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(final NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.from(exception));
    }

    @ExceptionHandler(TransactionFailureException.class)
    public ResponseEntity<ApiError> handleTransactionFailureException(final TransactionFailureException exception) {
        final var aOptimisticLockExceptionMessage = "Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)";

        if (exception.getMessage().contains(aOptimisticLockExceptionMessage)) {
            log.warn("An optimistic lock exception occurred while processing the transaction", exception);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.from("An optimistic lock exception occurred while processing the transaction"));
        }

        log.error("An error occurred while processing the transaction", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.from("An error occurred while processing the transaction"));
    }
}