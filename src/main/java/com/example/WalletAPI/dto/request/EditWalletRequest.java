package com.example.WalletAPI.dto.request;

import com.example.WalletAPI.model.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditWalletRequest {
    private UUID id;
    private OperationType operationType;
    private double amount;

}
