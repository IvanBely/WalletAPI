package com.example.WalletAPI.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class InvalidJsonException extends ResponseStatusException {
    public InvalidJsonException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
