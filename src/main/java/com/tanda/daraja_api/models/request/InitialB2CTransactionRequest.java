package com.tanda.daraja_api.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InitialB2CTransactionRequest {

    @JsonProperty("CommandID")
    public String commandID;

    @JsonProperty("Amount")
    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "10", message = "Amount must be at least 10")
    @DecimalMax(value = "150000", message = "Amount must not exceed 150,000")
    public float amount;

    @JsonProperty("PartyB")
    @Pattern(regexp = "^07[0-9]{8}$", message = "Invalid Safaricom mobile number")
    public String partyB;

    @JsonProperty("Remarks")
    public String remarks;

    @JsonProperty("Occassion")
    public String occassion;
}
