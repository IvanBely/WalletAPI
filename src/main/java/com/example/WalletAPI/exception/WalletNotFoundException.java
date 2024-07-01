package com.example.WalletAPI.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class WalletNotFoundException extends ResponseStatusException {
    public WalletNotFoundException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
