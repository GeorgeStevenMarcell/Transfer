package com.example.demo.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequestDTO {
    @NotNull
    private Long merchantId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String destinationAcct;
}
