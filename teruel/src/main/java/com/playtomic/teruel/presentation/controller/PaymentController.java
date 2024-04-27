package com.playtomic.teruel.presentation.controller;

import com.playtomic.teruel.application.CreatePaymentUseCase;
import com.playtomic.teruel.presentation.dto.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;

    public PaymentController(CreatePaymentUseCase createPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @PostMapping("/topup")
    public ResponseEntity<String> topUpWallet(@RequestBody PaymentRequest paymentRequest) {
        Long transactionId = createPaymentUseCase.topUpWallet(paymentRequest);
        return ResponseEntity.ok("Wallet topped up successfully and " +
                "transaction id created: " + transactionId);
    }

}
