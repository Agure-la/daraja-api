package com.tanda.daraja_api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReferenceData {

    @JsonProperty("ReferenceItem")
    public ReferenceItem referenceItem;

}
