package com.playtomic.teruel.domain.repository.paymentgateway;

import com.playtomic.teruel.domain.model.paymentgateway.PaymentGatewayProviderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentGatewayProviderLogRepository extends JpaRepository<PaymentGatewayProviderLog, Long> {

}
