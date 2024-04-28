package com.playtomic.teruel;

import com.playtomic.teruel.application.CreatePaymentUseCaseImp;
import com.playtomic.teruel.presentation.controller.PaymentController;
import com.playtomic.teruel.presentation.dto.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentControllerTest {

    private CreatePaymentUseCaseImp createPaymentUseCase;
    private PaymentController paymentController;

    @BeforeEach
    public void setUp() {
        createPaymentUseCase = mock(CreatePaymentUseCaseImp.class);
        paymentController = new PaymentController(createPaymentUseCase);
    }

    @Test
    public void testTopUpWallet_Success() {
        UUID userId = UUID.fromString("c2d29867-3d0b-d497-9191-18a9d8ee7830");
        BigDecimal amount = new BigDecimal("11");
        String creditCardNumber = "78979879";

        PaymentRequest paymentRequest = new PaymentRequest (
                userId,
                amount,
                creditCardNumber
        );
        when(createPaymentUseCase.topUpWallet(paymentRequest)).thenReturn(123L); // Assuming a transaction ID is returned

        ResponseEntity<String> responseEntity = paymentController.topUpWallet(paymentRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("Wallet topped up successfully");
        assertThat(responseEntity.getBody()).contains("transaction id created: 123");
    }
}
