package com.tanda.daraja_api.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Data
public class B2CRequest {

    @JsonProperty("OriginatorConversationID")
    public String originatorConversationID;

    @JsonProperty("InitiatorName")
    public String initiatorName;

    @JsonProperty("SecurityCredential")
    public String securityCredential;

    @JsonProperty("CommandID")
    public String commandID;

    @JsonProperty("Amount")
    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "10", message = "Amount must be at least 10")
    @DecimalMax(value = "150000", message = "Amount must not exceed 150,000")
    public float amount;

    @JsonProperty("PartyA")
    public String partyA;

    @JsonProperty("PartyB")
    @Pattern(regexp = "^07[0-9]{8}$", message = "Invalid Safaricom mobile number")
    public String partyB;

    @JsonProperty("Remarks")
    public String remarks;

    @JsonProperty("QueueTimeOutURL")
    public String queueTimeOutURL;

    @JsonProperty("ResultURL")
    public String resultURL;

    @JsonProperty("Occassion")
    public String occassion;

}
