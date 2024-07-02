package com.example.WalletAPI.service;

import com.example.WalletAPI.dto.request.EditWalletRequest;
import com.example.WalletAPI.model.Wallet;

import java.util.UUID;

public interface WalletService {
    void editWallet (EditWalletRequest request);
    Wallet findById (UUID walletId);
    void saveTransaction (Wallet wallet, String operationType, double amount);
    UUID generateUUID();

    void createWallet();
}
