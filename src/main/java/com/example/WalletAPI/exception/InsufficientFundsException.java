package com.example.WalletAPI.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class InsufficientFundsException extends ResponseStatusException {
    public InsufficientFundsException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
