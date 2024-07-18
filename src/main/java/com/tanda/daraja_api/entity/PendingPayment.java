package com.tanda.daraja_api.entity;

import com.tanda.daraja_api.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PendingPayment{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String transactionId;

    float amount;

}
