package com.tanda.daraja_api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReferenceItem {

    @JsonProperty("Key")
    public String key;

    @JsonProperty("Value")
    public String value;
}
