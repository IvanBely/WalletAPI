package com.example.WalletAPI.controllers;

import com.example.WalletAPI.dto.request.EditWalletRequest;
import com.example.WalletAPI.exception.InsufficientFundsException;
import com.example.WalletAPI.exception.InvalidJsonException;
import com.example.WalletAPI.exception.WalletNotFoundException;
import com.example.WalletAPI.model.Wallet;
import com.example.WalletAPI.model.repository.WalletRepository;
import com.example.WalletAPI.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("wallets/{WALLET_UUID}")
    public ResponseEntity<Wallet> getWalletBalance(@PathVariable UUID WALLET_UUID) {
        Wallet wallet = walletService.findById(WALLET_UUID);
        return ResponseEntity.ok(wallet);
    }
    @PostMapping("/")
    public ResponseEntity<String> crateWallet() {
        walletService.createWallet();
        return ResponseEntity.ok("Кошелек успешно создан");
    }
    @PostMapping("/wallet")
    public ResponseEntity<String> processTransaction(@RequestBody EditWalletRequest request) {
        walletService.editWallet(request);
        return ResponseEntity.ok("Кошелек успешно обновлен");
    }
}

