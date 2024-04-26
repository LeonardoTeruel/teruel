package com.playtomic.teruel.domain.rest;

import com.playtomic.teruel.domain.model.paymentgateway.Payment;

import java.math.BigDecimal;

public interface PaymentRest {

     Payment charge (String creditCardNumber, BigDecimal amount);

     void refund (String paymentId);
}
