package com.playtomic.teruel.presentation.controller;

import com.playtomic.teruel.application.CreatePaymentUseCase;
import com.playtomic.teruel.domain.exception.paymentgateway.PaymentGatewayException;
import com.playtomic.teruel.domain.exception.wallet.WalletNotFoundException;
import com.playtomic.teruel.presentation.dto.PaymentRequest;
import com.playtomic.teruel.presentation.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;

    public PaymentController(CreatePaymentUseCase createPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @PostMapping("/topup")
    public ResponseEntity<String> topUpWallet(@RequestBody PaymentRequest paymentRequest) {

        try {

        Long transactionId = createPaymentUseCase.topUpWallet(paymentRequest);
        return ResponseEntity.ok("Wallet topped up successfully and " +
                                            "transaction id created: " + transactionId);

        } catch (PaymentGatewayException ex) {
            return ResponseEntity.status(ex.getErrorResponse().getStatus())
                    .body(ex.getErrorResponse().getMessage());

        } catch (WalletNotFoundException ex) {
            return ResponseEntity.status(ex.getErrorResponse().getStatus())
                    .body(ex.getErrorResponse().getMessage());
        }
    }

}
