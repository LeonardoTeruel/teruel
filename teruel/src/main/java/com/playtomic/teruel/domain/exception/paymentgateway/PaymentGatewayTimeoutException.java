package com.playtomic.teruel.domain.exception.paymentgateway;

import com.playtomic.teruel.presentation.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class PaymentGatewayTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PaymentGatewayTimeoutException(String message) {
        super(message);
    }

    public PaymentGatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, getMessage(), LocalDateTime.now());
    }
}
