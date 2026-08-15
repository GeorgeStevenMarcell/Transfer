package com.example.demo.dtos;

import com.example.demo.enums.TransactionStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TransferResponseDTO {

    @NotNull
    private UUID transactionId;

    @NotNull
    private TransactionStatusEnum status;

}
