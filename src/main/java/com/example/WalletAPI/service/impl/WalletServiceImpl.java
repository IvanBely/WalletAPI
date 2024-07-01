package com.example.WalletAPI.service.impl;

import com.example.WalletAPI.exception.InsufficientFundsException;
import com.example.WalletAPI.dto.request.EditWalletRequest;
import com.example.WalletAPI.exception.InvalidJsonException;
import com.example.WalletAPI.exception.WalletNotFoundException;
import com.example.WalletAPI.model.OperationType;
import com.example.WalletAPI.model.Wallet;
import com.example.WalletAPI.model.repository.WalletRepository;
import com.example.WalletAPI.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet findById (UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(HttpStatus.NOT_FOUND, "Кошелек не найден"));
        return wallet;
    }
    @Override
    public void editWallet(EditWalletRequest request) throws WalletNotFoundException, InvalidJsonException, InsufficientFundsException {
        UUID walletId = request.getId();
        OperationType operationType = request.getOperationType();
        double amount = request.getAmount();

        if (walletId == null || operationType == null || amount <= 0) {
            throw new InvalidJsonException(HttpStatus.BAD_REQUEST, "Неверный формат запроса");
        }

        Wallet wallet = findById(request.getId());

        if (OperationType.DEPOSIT.equals(operationType)) {
            wallet.setBalance(wallet.getBalance() + amount);
        } else if (OperationType.WITHDRAW.equals(operationType)) {
            if (wallet.getBalance() < amount) {
                throw new InsufficientFundsException(HttpStatus.PAYMENT_REQUIRED, "Недостаточно средств");
            }
            wallet.setBalance(wallet.getBalance() - amount);
        }
        walletRepository.save(wallet);
    }
}
