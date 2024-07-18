package com.tanda.daraja_api.entity;

import com.tanda.daraja_api.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String phone;

    String name;

    @Enumerated(EnumType.STRING)
    PaymentStatus Status;

    String transactionId;

    float amount;
}
