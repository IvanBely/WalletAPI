package com.example.WalletAPI.service.impl;

import com.example.WalletAPI.exception.InsufficientFundsException;
import com.example.WalletAPI.dto.request.EditWalletRequest;
import com.example.WalletAPI.exception.InvalidJsonException;
import com.example.WalletAPI.exception.WalletNotFoundException;
import com.example.WalletAPI.model.OperationType;
import com.example.WalletAPI.model.Transaction;
import com.example.WalletAPI.model.Wallet;
import com.example.WalletAPI.model.repository.TransactionRepository;
import com.example.WalletAPI.model.repository.WalletRepository;
import com.example.WalletAPI.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    private final ConcurrentHashMap<UUID, BlockingQueue<Runnable>> walletQueues = new ConcurrentHashMap<>();

    @Override
    @Transactional(readOnly = true)
    public Wallet findById (UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек не найден"));
        return wallet;
    }
    @Override
    @Transactional
    public void editWallet(EditWalletRequest request) throws WalletNotFoundException, InvalidJsonException, InsufficientFundsException {
        UUID walletId = request.getId();
        walletQueues.computeIfAbsent(walletId, id -> new LinkedBlockingQueue<>()).add(() -> {
            try {
                processEditWallet(request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        processQueue(walletId);
    }
    private void processQueue(UUID walletId) {
        BlockingQueue<Runnable> queue = walletQueues.get(walletId);

        Runnable task;
        while ((task = queue.poll()) != null) {
            task.run();
        }
        walletQueues.remove(walletId, queue);
    }

    private void processEditWallet(EditWalletRequest request) throws WalletNotFoundException, InvalidJsonException, InsufficientFundsException {
        UUID walletId = request.getId();
        OperationType operationType = request.getOperationType();
        double amount = request.getAmount();

        if (walletId == null) {
            throw new InvalidJsonException("Не указан walletId кошелька");
        }

        if (operationType == null) {
            throw new InvalidJsonException("Не указан operationType");
        }

        if (amount <= 0) {
            throw new InvalidJsonException("Сумма не должна быть отрицательной");
        }

        Wallet wallet = findById(request.getId());

        if (OperationType.DEPOSIT.equals(operationType)) {
            wallet.setBalance(wallet.getBalance() + amount);
        } else if (OperationType.WITHDRAW.equals(operationType)) {
            if (wallet.getBalance() < amount) {
                throw new InsufficientFundsException("Недостаточно средств");
            }
            wallet.setBalance(wallet.getBalance() - amount);
        }
        walletRepository.save(wallet);

        saveTransaction(wallet, operationType.name(), amount);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTransaction(Wallet wallet, String operationType, double amount) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateUUID());
        transaction.setWallet(wallet);
        transaction.setOperationType(operationType);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
    @Override
    public UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Override
    @Transactional
    public void createWallet() {
        Wallet wallet = new Wallet();
        wallet.setWalletId(generateUUID());
        wallet.setBalance(0);
        walletRepository.save(wallet);
    }
}
