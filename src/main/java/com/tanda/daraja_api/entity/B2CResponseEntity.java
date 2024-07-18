package com.tanda.daraja_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
public class B2CResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String conversationID;
    private String originatorConversationID;
    private String responseDescription;
    private int status;
    private int resultCode;
    private String resultDesc;
    private int resultType;
    private String transactionId;
    private float transactionAmount;
    private String transactionCompletedDateTime;
    private String receiverPartyPublicName;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}
