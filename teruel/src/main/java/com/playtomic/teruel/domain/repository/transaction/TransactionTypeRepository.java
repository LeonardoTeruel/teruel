package com.playtomic.teruel.domain.repository.transaction;

import com.playtomic.teruel.domain.model.transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {

}
