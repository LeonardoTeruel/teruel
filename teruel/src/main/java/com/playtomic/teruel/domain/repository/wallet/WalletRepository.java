package com.playtomic.teruel.domain.repository.wallet;

import com.playtomic.teruel.domain.model.wallet.Wallet;
import com.playtomic.teruel.domain.model.wallet.WalletId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletId> {

}
