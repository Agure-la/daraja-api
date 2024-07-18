package com.tanda.daraja_api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Result {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("ResultType")
    public int resultType;

    @JsonProperty("ResultCode")
    public int resultCode;

    @JsonProperty("ResultDesc")
    public String resultDesc;

    @JsonProperty("OriginatorConversationID")
    public String originatorConversationID;

    @JsonProperty("ConversationID")
    public String conversationID;

    @JsonProperty("TransactionID")
    public String transactionID;

    @JsonProperty("ReferenceData")
    ReferenceData referenceData;

    @JsonProperty("ResultParameters")
    List<ReferenceItem> referenceItems;
}
