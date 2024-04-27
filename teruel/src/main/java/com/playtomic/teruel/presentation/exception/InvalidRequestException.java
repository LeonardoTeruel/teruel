package com.playtomic.teruel.presentation.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class InvalidRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, getMessage(), LocalDateTime.now());
    }
}
