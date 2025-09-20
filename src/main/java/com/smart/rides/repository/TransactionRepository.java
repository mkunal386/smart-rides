package com.smart.rides.repository;

import com.smart.rides.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Find all transactions for a given user, ordered by most recent first
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);
}
