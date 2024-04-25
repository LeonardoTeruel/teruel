package com.playtomic.teruel.domain.repository;

import com.playtomic.teruel.domain.model.Wallet;
import com.playtomic.teruel.domain.model.WalletId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletId> {

}
