package com.tanda.daraja_api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AcknowledgeResponse {
    private String message;

    boolean status;
}
