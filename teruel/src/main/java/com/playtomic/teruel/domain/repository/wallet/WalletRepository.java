package com.playtomic.teruel.domain.repository.wallet;

import com.playtomic.teruel.domain.model.wallet.Wallet;
import com.playtomic.teruel.domain.model.wallet.WalletId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletId> {

    public List<Wallet> findByIdUserId(UUID userId);

}
