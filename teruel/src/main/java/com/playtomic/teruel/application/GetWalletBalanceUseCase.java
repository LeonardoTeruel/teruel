package com.playtomic.teruel.application;

import com.playtomic.teruel.domain.exception.wallet.WalletNotFoundException;
import com.playtomic.teruel.domain.model.wallet.Wallet;
import com.playtomic.teruel.domain.repository.wallet.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GetWalletBalanceUseCase {

    private final WalletRepository walletRepository;

    public GetWalletBalanceUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public BigDecimal getWalletBalance(UUID userId) {

        List<Wallet> walletList = walletRepository.findByIdUserId(userId);

        if (walletList == null || walletList.isEmpty()) {
           throw new WalletNotFoundException("Wallet not found for user with ID: " + userId.toString());
        }

        return walletList.getFirst().getBalance();
    }
}
