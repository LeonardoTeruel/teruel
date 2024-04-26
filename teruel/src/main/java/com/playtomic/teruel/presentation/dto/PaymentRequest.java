package com.playtomic.teruel.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest (UUID userId, BigDecimal amount, String creditCardNumber){

}
