package com.example.WalletAPI.controllers;

import com.example.WalletAPI.dto.request.EditWalletRequest;
import com.example.WalletAPI.exception.InsufficientFundsException;
import com.example.WalletAPI.exception.InvalidJsonException;
import com.example.WalletAPI.exception.WalletNotFoundException;
import com.example.WalletAPI.model.OperationType;
import com.example.WalletAPI.model.Wallet;
import com.example.WalletAPI.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @MockBean
    private WalletService walletService;

    @Autowired
    private WalletController walletController;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUpp() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void getWalletBalance_OK() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, 100.0);

        when(walletService.findById(walletId)).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(100.0));
    }
    @Test
    void getWalletBalance_NOT_FOUND() throws Exception {
        UUID walletId = UUID.randomUUID();
        when(walletService.findById(walletId)).thenThrow(new WalletNotFoundException(HttpStatus.NOT_FOUND, "Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("404 NOT_FOUND \"Wallet not found\""));
    }
    @Test
    void createWallet_OK() throws Exception {
        ResponseEntity<String> responseEntity = walletController.crateWallet();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Кошелек успешно создан", responseEntity.getBody());
    }
    @Test
    void processTransaction_OK() throws Exception {
        EditWalletRequest request = new EditWalletRequest(UUID.randomUUID(), OperationType.DEPOSIT, 1000);
        ResponseEntity<String> responseEntity = walletController.processTransaction(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Кошелек успешно обновлен", responseEntity.getBody());
    }
    @Test
    void processTransaction_InvalidJsonException() throws Exception {
        EditWalletRequest request = new EditWalletRequest(UUID.randomUUID(), OperationType.DEPOSIT, 1000);
        String requestJson = objectMapper.writeValueAsString(request);

        doThrow(new InvalidJsonException(HttpStatus.BAD_REQUEST, "Invalid request"))
                .when(walletService).editWallet(request);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("400 BAD_REQUEST \"Invalid request\""));
    }
    @Test
    void processTransaction_WalletNotFoundException() throws Exception {
        EditWalletRequest request = new EditWalletRequest(UUID.randomUUID(), OperationType.DEPOSIT, 1000);
        String requestJson = objectMapper.writeValueAsString(request);

        doThrow(new WalletNotFoundException(HttpStatus.NOT_FOUND, "Wallet not found"))
                .when(walletService).editWallet(request);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("404 NOT_FOUND \"Wallet not found\""));
    }
    @Test
    void processTransaction_InsufficientFundsException() throws Exception {
        EditWalletRequest request = new EditWalletRequest(UUID.randomUUID(), OperationType.WITHDRAW, 1000);
        String requestJson = objectMapper.writeValueAsString(request);

        doThrow(new InsufficientFundsException(HttpStatus.PAYMENT_REQUIRED, "Insufficient funds"))
                .when(walletService).editWallet(request);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isPaymentRequired())
                .andExpect(content().string("402 PAYMENT_REQUIRED \"Insufficient funds\""));
    }
}
