package com.playtomic.teruel.presentation.controller;

import com.playtomic.teruel.application.GetWalletBalanceUseCase;
import com.playtomic.teruel.domain.exception.wallet.WalletNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final GetWalletBalanceUseCase getWalletBalanceUseCase;

    public WalletController(GetWalletBalanceUseCase getWalletBalanceUseCase) {
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getWalletBalance(@PathVariable UUID userId) {
        try {
            BigDecimal balance = getWalletBalanceUseCase.getWalletBalance(userId);
            return ResponseEntity.ok(balance);

        } catch (WalletNotFoundException ex) {
            return ResponseEntity.status(ex.getErrorResponse().getStatus())
                    .body(ex.getErrorResponse().getMessage());
        }

    }
}
