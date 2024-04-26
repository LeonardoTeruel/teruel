package com.playtomic.teruel.domain.exception.wallet;

import com.playtomic.teruel.presentation.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class WalletNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WalletNotFoundException(String message) {
        super(message);
    }

    public WalletNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(HttpStatus.NOT_FOUND, getMessage(), LocalDateTime.now());
    }
}
