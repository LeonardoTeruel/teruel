package com.playtomic.teruel;

import com.playtomic.teruel.application.CreatePaymentUseCase;
import com.playtomic.teruel.domain.exception.paymentgateway.PaymentGatewayException;
import com.playtomic.teruel.domain.model.transaction.Transaction;
import com.playtomic.teruel.domain.model.wallet.Wallet;
import com.playtomic.teruel.domain.repository.paymentgateway.PaymentGatewayProviderLogRepository;
import com.playtomic.teruel.domain.repository.transaction.TransactionRepository;
import com.playtomic.teruel.domain.repository.wallet.WalletRepository;
import com.playtomic.teruel.domain.rest.PaymentRest;
import com.playtomic.teruel.infrastructure.external.stripe.dto.StripePaymentResponse;
import com.playtomic.teruel.presentation.dto.PaymentRequest;
import com.playtomic.teruel.presentation.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class CreatePaymentUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentGatewayProviderLogRepository paymentGatewayProviderLogRepository;

    @Mock
    private PaymentRest paymentRest;

    @InjectMocks
    private CreatePaymentUseCase createPaymentUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void topUpWallet_Successful() {
        UUID userId = UUID.fromString("c2d29867-3d0b-d497-9191-18a9d8ee7830");
        String creditCardNumber = "78979879";
        String paymentChargeResponse = "198";

        PaymentRequest paymentRequest = new PaymentRequest(userId,
                BigDecimal.valueOf(15), creditCardNumber);

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.TEN);

        when(walletRepository.findByIdUserId(paymentRequest.userId()))
                .thenReturn(List.of(wallet));

        when(paymentRest.charge(paymentRequest.creditCardNumber(), paymentRequest.amount()))
                .thenReturn(new StripePaymentResponse(paymentChargeResponse));

        Long transactionId = createPaymentUseCase.topUpWallet(paymentRequest);

        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void topUpWallet_WithAmountLessThanMinimum_ShouldThrowException() {
        UUID userId = UUID.fromString("c2d29867-3d0b-d497-9191-18a9d8ee7830");
        String creditCardNumber = "78979879";

        // Amount less than the minimum required amount (10 euro)
        BigDecimal amount = BigDecimal.valueOf(5);

        PaymentRequest paymentRequest = new PaymentRequest(userId, amount, creditCardNumber);

        // Then
        assertThrows(InvalidRequestException.class, () -> {
            createPaymentUseCase.topUpWallet(paymentRequest);
        });
    }

    @Test
    void topUpWallet_FailureInProcessingPayment_ShouldThrowPaymentGatewayException() {
        UUID userId = UUID.fromString("c2d29867-3d0b-d497-9191-18a9d8ee7830");
        String creditCardNumber = "78979879";
        BigDecimal amount = BigDecimal.valueOf(15);

        PaymentRequest paymentRequest = new PaymentRequest(userId, amount, creditCardNumber);

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.TEN);

        when(walletRepository.findByIdUserId(paymentRequest.userId()))
                .thenReturn(List.of(wallet));

        when(paymentRest.charge(paymentRequest.creditCardNumber(), paymentRequest.amount()))
                .thenThrow(new PaymentGatewayException("Failed to process payment"));

        // Then
        assertThrows(PaymentGatewayException.class, () -> {
            createPaymentUseCase.topUpWallet(paymentRequest);
        });

        // Verify that save methods were not called
        verify(paymentGatewayProviderLogRepository, never()).save(any());
        verify(walletRepository, never()).save(any());
    }
}