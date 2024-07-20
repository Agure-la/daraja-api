package com.tanda.daraja_api.entity;


import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class B2CRequestEntity {

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
    private String callBackUrl;
    private String uuid;
}
