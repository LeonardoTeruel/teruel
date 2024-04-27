package com.playtomic.teruel.application;

import com.playtomic.teruel.domain.model.paymentgateway.Payment;
import com.playtomic.teruel.domain.model.paymentgateway.PaymentGatewayProviderLog;
import com.playtomic.teruel.domain.model.transaction.Transaction;
import com.playtomic.teruel.domain.model.transaction.TransactionStatus;
import com.playtomic.teruel.domain.model.transaction.TransactionType;
import com.playtomic.teruel.domain.model.transaction.TransactionTypeEnum;
import com.playtomic.teruel.domain.model.wallet.Wallet;
import com.playtomic.teruel.domain.repository.paymentgateway.PaymentGatewayProviderLogRepository;
import com.playtomic.teruel.domain.repository.transaction.TransactionRepository;
import com.playtomic.teruel.domain.repository.wallet.WalletRepository;
import com.playtomic.teruel.domain.rest.PaymentRest;
import com.playtomic.teruel.presentation.dto.PaymentRequest;
import com.playtomic.teruel.presentation.exception.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CreatePaymentUseCase {

    private final String MINIMUM_AMOUNT_VALIDATION_MESSAGE = "Amount must be at least 10 euro.";

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentGatewayProviderLogRepository paymentGatewayProviderLogRepository;
    private final PaymentRest paymentRest;

    public CreatePaymentUseCase(WalletRepository walletRepository,
                                    TransactionRepository transactionRepository,
                                        PaymentGatewayProviderLogRepository paymentGatewayProviderLogRepository,
                                             PaymentRest paymentRest) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.paymentGatewayProviderLogRepository = paymentGatewayProviderLogRepository;
        this.paymentRest = paymentRest;
    }

    @Transactional
    public Long topUpWallet(PaymentRequest paymentRequest)  {

        validatePaymentRequest(paymentRequest);

        List<Wallet> walletList = walletRepository.findByIdUserId(paymentRequest.userId());
        Wallet wallet = walletList.getFirst();

        TransactionType transactionType = new TransactionType (TransactionTypeEnum.TOP_UP.getTypeId(),
                TransactionTypeEnum.TOP_UP.getName());

        // Record transaction
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(paymentRequest.amount())
                .type(transactionType)
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);

        // Process payment using the payment gateway provider (Stripe API in this case)
        Payment paymentChargeId = paymentRest.charge(paymentRequest.creditCardNumber(),
              paymentRequest.amount());

        PaymentGatewayProviderLog paymentGatewayProviderLog = PaymentGatewayProviderLog.builder()
                        .transaction(transaction)
                        .gatewayProviderResponse(paymentChargeId.toString())
                        .build();

        paymentGatewayProviderLogRepository.save(paymentGatewayProviderLog);

        walletList.getFirst().setBalance(wallet.getBalance().add(transaction.getAmount()));
        walletRepository.save(wallet);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        return transaction.getId();
    }

    private void validatePaymentRequest(PaymentRequest paymentRequest) {
        validateMinimumAmount(paymentRequest.amount());
    }

    private void validateMinimumAmount (BigDecimal amount) throws InvalidRequestException {
        BigDecimal minimumAmount = BigDecimal.TEN; // Minimum amount is 10 euros
        if (amount.compareTo(minimumAmount) < 0) {
            throw new InvalidRequestException(MINIMUM_AMOUNT_VALIDATION_MESSAGE);
        }
    }
}
