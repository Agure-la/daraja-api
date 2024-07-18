package com.tanda.daraja_api.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class C2BConfigurations {
    private String validationUrl;

    private String confirmationUrl;
}
