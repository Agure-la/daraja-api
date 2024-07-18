package com.tanda.daraja_api.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
public class GwRequest {

    @JsonProperty("id")
    private String id;

    @JsonProperty("amount")
    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "10", message = "Amount must be at least 10")
    @DecimalMax(value = "150000", message = "Amount must not exceed 150,000")
    public float amount;

    @JsonProperty("mobile_number")
   // @Pattern(regexp = "^07[0-9]{8}$", message = "Invalid Safaricom mobile number")
    public String mobileNumber;

}
