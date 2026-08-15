package com.example.demo.enums;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public enum TransactionStatusEnum {
    PENDING(0),
    SUCCESS(1),
    FAILED(-1);

    @Enumerated()
    private final int status;

    private TransactionStatusEnum(int status){
        this.status = status;
    }

    public int getStatus(){
        return this.status;
    }
}
