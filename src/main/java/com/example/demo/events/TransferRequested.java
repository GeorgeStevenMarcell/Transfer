package com.example.demo.events;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequested(
        UUID transactionId,
        Long merchantId,
        BigDecimal amount,
        String destinationAcct
) {
}
