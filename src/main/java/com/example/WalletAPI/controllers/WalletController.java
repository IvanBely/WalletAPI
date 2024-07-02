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
    public ResponseEntity<Object> getWalletBalance(@PathVariable UUID WALLET_UUID) {
        try {
            Wallet wallet = walletService.findById(WALLET_UUID);
            return ResponseEntity.ok(wallet);
        } catch (WalletNotFoundException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }
    @PostMapping("/")
    public ResponseEntity<String> crateWallet() {
        try {
            walletService.createWallet();
            return ResponseEntity.status(HttpStatus.CREATED).body("Кошелек успешно создан");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка обработки запроса");
        }
    }
    @PostMapping("/wallet")
    public ResponseEntity<String> processTransaction(@RequestBody EditWalletRequest request) {
        try {
            walletService.editWallet(request);
            return ResponseEntity.status(HttpStatus.OK).body("Кошелек успешно обновлен");
        } catch (InvalidJsonException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (WalletNotFoundException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка обработки транзакции");
        }
    }
}

