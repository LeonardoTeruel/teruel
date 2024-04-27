package com.playtomic.teruel.application;

import com.playtomic.teruel.domain.exception.paymentgateway.PaymentGatewayException;
import com.playtomic.teruel.domain.exception.wallet.WalletNotFoundException;
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
import com.playtomic.teruel.presentation.exception.TransactionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CreatePaymentUseCase {

    private final String MINIMUM_AMOUNT_VALIDATION_MESSAGE = "Amount must be at least 10 euro";
    private final String WALLET_NOT_FOUND_MESSAGE = "Wallet not found for user ID: ";
    private final String PAYMENT_GATEWAY_PROVIDER_ERROR_MESSAGE = "Failed in processing with Payment Gateway Provider";
    private final String DATA_ACCESS_LEVEL_ERROR_MESSAGE = "Failed to process transaction on Data Access level";


    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentGatewayProviderLogRepository paymentGatewayProviderLogRepository;
    private final PaymentRest paymentRest;
    private final ReentrantLock lock = new ReentrantLock();

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
    public Long topUpWallet(PaymentRequest paymentRequest) {

        lock.lock();
        try {

            validatePaymentRequest(paymentRequest);

            List<Wallet> walletList = walletRepository.findByIdUserId(paymentRequest.userId());
            if (walletList.isEmpty()) {
                throw new WalletNotFoundException(WALLET_NOT_FOUND_MESSAGE + paymentRequest.userId());
            }

            Wallet wallet = walletList.getFirst();

            TransactionType transactionType = new TransactionType(TransactionTypeEnum.TOP_UP.getTypeId(),
                    TransactionTypeEnum.TOP_UP.getName());

            // Record transaction
            Transaction transaction = Transaction.builder()
                    .wallet(wallet)
                    .amount(paymentRequest.amount())
                    .type(transactionType)
                    .status(TransactionStatus.PENDING)
                    .build();

            try {
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

            } catch (PaymentGatewayException ex) {
                throw new PaymentGatewayException(PAYMENT_GATEWAY_PROVIDER_ERROR_MESSAGE, ex);

            } catch (DataAccessException ex) {
                throw new TransactionFailedException(DATA_ACCESS_LEVEL_ERROR_MESSAGE, ex);
            }

        } finally {
            lock.unlock();
        }
    }

    private void validatePaymentRequest(PaymentRequest paymentRequest) {
        validateMinimumAmount(paymentRequest.amount());
    }

    private void validateMinimumAmount(BigDecimal amount) throws InvalidRequestException {
        BigDecimal minimumAmount = BigDecimal.TEN; // Minimum amount is 10 euros
        if (amount.compareTo(minimumAmount) < 0) {
            throw new InvalidRequestException(MINIMUM_AMOUNT_VALIDATION_MESSAGE);
        }
    }
}
