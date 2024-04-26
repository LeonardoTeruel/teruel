package com.playtomic.teruel.infrastructure.database;

import com.playtomic.teruel.domain.model.transaction.Transaction;
import com.playtomic.teruel.domain.repository.transaction.TransactionRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostgresTransactionRepository  extends TransactionRepository, JpaRepository<Transaction, Long> {

}
