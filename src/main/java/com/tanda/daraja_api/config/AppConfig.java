package com.tanda.daraja_api.config;

import com.tanda.daraja_api.models.AcknowledgeResponse;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
    @Bean
    public AcknowledgeResponse getAknowledgeResponse(){
        AcknowledgeResponse acknowledgeResponse =new AcknowledgeResponse("success", false);
        return acknowledgeResponse;
    }
}
