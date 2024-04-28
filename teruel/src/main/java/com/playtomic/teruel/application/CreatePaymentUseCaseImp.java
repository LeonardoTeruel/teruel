package com.playtomic.teruel.application;

import com.playtomic.teruel.domain.exception.paymentgateway.PaymentGatewayException;
import com.playtomic.teruel.domain.exception.paymentgateway.PaymentGatewayTimeoutException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CreatePaymentUseCaseImp implements CreatePaymentUseCase {

    private final String MINIMUM_AMOUNT_VALIDATION_MESSAGE = "Amount must be at least 10 euro";
    private final String WALLET_NOT_FOUND_MESSAGE = "Wallet not found for user ID: ";
    private final String PAYMENT_GATEWAY_PROVIDER_ERROR_MESSAGE = "Failed in processing with Payment Gateway Provider";
    private final String DATA_ACCESS_LEVEL_ERROR_MESSAGE = "Failed to process transaction on Data Access level";
    private final String PAYMENT_GATEWAY_TIMEOUT_MESSAGE = "The Transaction is pending and it will be confirmed soon";

    private static final Logger logger = LoggerFactory.getLogger(CreatePaymentUseCaseImp.class);

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentGatewayProviderLogRepository paymentGatewayProviderLogRepository;
    private final PaymentRest paymentRest;
    private final ReentrantLock lock = new ReentrantLock();

    public CreatePaymentUseCaseImp(WalletRepository walletRepository,
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

            logger.info("Starting Validations for TopUp wallet process for userId: {}",
                    paymentRequest.userId());

            validatePaymentRequest(paymentRequest);

            List<Wallet> walletList = walletRepository.findByIdUserId(paymentRequest.userId());
            if (walletList.isEmpty()) {
                throw new WalletNotFoundException(WALLET_NOT_FOUND_MESSAGE + paymentRequest.userId());
            }

            Wallet wallet = walletList.getFirst();

            TransactionType transactionType = new TransactionType(TransactionTypeEnum.TOP_UP.getTypeId(),
                    TransactionTypeEnum.TOP_UP.getName());

            /* An idempotency key could be generated here for the transaction
               and to be sent to the payment gateway provider to help avoiding duplication issues
                but as this wasn't implemented in the given service of the Stripe, I didn't implement
                it to use that as it was given */

            Transaction transaction = Transaction.builder()
                    .wallet(wallet)
                    .amount(paymentRequest.amount())
                    .type(transactionType)
                    .status(TransactionStatus.PENDING)
                    .build();

            transactionRepository.save(transaction);
            logger.info("Pending Transaction created with transactionId: {}", transaction.getId());

            try {
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
                logger.info("Wallet balance updated: {}", wallet.getBalance());


                transaction.setStatus(TransactionStatus.COMPLETED);
                transactionRepository.save(transaction);
                logger.info("Transaction COMPLETED with transactionId: {}", transaction.getId());

                return transaction.getId();

            } catch (PaymentGatewayException ex) {
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                logger.info("Transaction FAILED with transactionId: {}", transaction.getId());
                throw new PaymentGatewayException(PAYMENT_GATEWAY_PROVIDER_ERROR_MESSAGE, ex);

            } catch (PaymentGatewayTimeoutException ex) {
                logger.error("Payment Gateway Timeout: {}", ex.getMessage(), ex);
                /* Do nothing, leave the transaction in pending status
                 to make a reconciliation with the provider and confirm if it was completed or failed.
                 Return message to the client saying the transaction is pending confirmation */
                throw new PaymentGatewayTimeoutException(PAYMENT_GATEWAY_TIMEOUT_MESSAGE, ex);

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
