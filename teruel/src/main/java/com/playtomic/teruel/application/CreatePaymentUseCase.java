package com.playtomic.teruel.application;

import com.playtomic.teruel.domain.model.paymentgateway.Payment;
import com.playtomic.teruel.domain.model.transaction.Transaction;
import com.playtomic.teruel.domain.model.transaction.TransactionStatus;
import com.playtomic.teruel.domain.model.transaction.TransactionType;
import com.playtomic.teruel.domain.model.transaction.TransactionTypeEnum;
import com.playtomic.teruel.domain.model.wallet.Wallet;
import com.playtomic.teruel.domain.repository.transaction.TransactionRepository;
import com.playtomic.teruel.domain.repository.wallet.WalletRepository;
import com.playtomic.teruel.domain.rest.PaymentRest;
import com.playtomic.teruel.presentation.dto.PaymentRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreatePaymentUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentRest paymentRest;

    public CreatePaymentUseCase(WalletRepository walletRepository,
                          TransactionRepository transactionRepository, PaymentRest paymentRest) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.paymentRest = paymentRest;
    }

    @Transactional
    public Long topUpWallet(PaymentRequest paymentRequest)  {

        List<Wallet> walletList = walletRepository.findByIdUserId(paymentRequest.userId());
        Wallet wallet = walletList.getFirst();

        TransactionType transactionType = new TransactionType (TransactionTypeEnum.TOP_UP.getTypeId(),
                TransactionTypeEnum.TOP_UP.getName());

        // Record transaction
        Transaction transaction = new Transaction(wallet, paymentRequest.amount(),
                transactionType,TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        // Process payment using the payment gateway provider (Stripe API in this case)
        Payment paymentChargeId = paymentRest.charge(paymentRequest.creditCardNumber(),
              paymentRequest.amount());

        walletList.getFirst().setBalance(wallet.getBalance().add(transaction.getAmount()));
        walletRepository.save(wallet);

        return transaction.getId();
    }
}
