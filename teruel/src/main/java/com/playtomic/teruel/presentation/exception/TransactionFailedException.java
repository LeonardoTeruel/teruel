package com.playtomic.teruel.presentation.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class TransactionFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, getMessage(), LocalDateTime.now());
    }
}
