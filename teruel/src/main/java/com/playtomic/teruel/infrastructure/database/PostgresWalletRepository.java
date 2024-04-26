package com.playtomic.teruel.infrastructure.database;

import com.playtomic.teruel.domain.model.wallet.Wallet;
import com.playtomic.teruel.domain.model.wallet.WalletId;
import com.playtomic.teruel.domain.repository.wallet.WalletRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostgresWalletRepository extends WalletRepository, JpaRepository<Wallet, WalletId> {
}
