package com.tanda.daraja_api.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
@AllArgsConstructor
public class GwRequest {

    @NotEmpty(message = "UUID must not be empty")
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("amount")
    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "10", message = "Amount must be at least 10")
    @DecimalMax(value = "150000", message = "Amount must not exceed 150,000")
    public float amount;

    @JsonProperty("mobile_number")
    @NotEmpty(message = "Mobile number must not be empty")
   // @Pattern(regexp = "^07[0-9]{8}$", message = "Invalid Safaricom mobile number")
    public String mobileNumber;

    @JsonProperty("call_url")
    public String callBackUrl;

}
