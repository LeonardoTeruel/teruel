package com.playtomic.teruel.application;

import com.playtomic.teruel.presentation.dto.PaymentRequest;

public interface CreatePaymentUseCase {

    Long topUpWallet(PaymentRequest paymentRequest);
}
