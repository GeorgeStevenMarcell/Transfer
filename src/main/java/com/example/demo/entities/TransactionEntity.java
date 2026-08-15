package com.example.demo.entities;

import com.example.demo.enums.TransactionStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @Column(unique = true)
    private UUID transactionId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String destinationAcct;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;

    private String failureReason;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    private Long merchantId;
}
