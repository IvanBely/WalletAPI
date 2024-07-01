package com.example.WalletAPI.model.repository;

import com.example.WalletAPI.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository <Transaction, UUID> {
}

