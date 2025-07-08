package com.homework.rewards.api.repository;

import com.homework.rewards.api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
} 