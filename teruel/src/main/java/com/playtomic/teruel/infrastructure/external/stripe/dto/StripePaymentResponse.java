package com.playtomic.teruel.infrastructure.external.stripe.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.playtomic.teruel.domain.model.paymentgateway.Payment;
import lombok.Data;
import lombok.NonNull;

@Data
public class StripePaymentResponse implements Payment {

    @NonNull
    private String id;

    @JsonCreator
    public StripePaymentResponse(@JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }

}
