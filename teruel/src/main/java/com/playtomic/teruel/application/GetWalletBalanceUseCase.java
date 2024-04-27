package com.playtomic.teruel.application;

import com.playtomic.teruel.domain.exception.wallet.WalletNotFoundException;
import com.playtomic.teruel.domain.model.wallet.Wallet;
import com.playtomic.teruel.domain.repository.wallet.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GetWalletBalanceUseCase {

    private final String WALLET_NOT_FOUND_MESSAGE = "Wallet not found for user ID: ";

    private static final Logger logger = LoggerFactory.getLogger(CreatePaymentUseCase.class);

    private final WalletRepository walletRepository;

    public GetWalletBalanceUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public BigDecimal getWalletBalance(UUID userId) {
        logger.info("Getting wallet balance");
        List<Wallet> walletList = walletRepository.findByIdUserId(userId);

        if (walletList == null || walletList.isEmpty()) {
           throw new WalletNotFoundException(WALLET_NOT_FOUND_MESSAGE + userId.toString());
        }

        logger.info("Returning wallet balance: {}", walletList.getFirst().getBalance());
        return walletList.getFirst().getBalance();
    }
}
