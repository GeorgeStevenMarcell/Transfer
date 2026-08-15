package com.example.demo.enums;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public enum TransactionStatusEnum {
    PENDING,
    SUCCESS,
    FAILED;
}
