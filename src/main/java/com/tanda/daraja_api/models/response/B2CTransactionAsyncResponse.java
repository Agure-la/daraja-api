package com.tanda.daraja_api.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tanda.daraja_api.models.Result;
import lombok.Data;

@Data
public class B2CTransactionAsyncResponse {

    @JsonProperty("Result")
    public Result result;
}
