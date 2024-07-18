package com.tanda.daraja_api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

public class HelperUtility {

    public static  String toJson(Object object){
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String encodeToBase64(String data) {
        // Encoding to Base64
        byte[] encodedBytes = Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static String generateTransactionTimestamp(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return currentDateTime.format(formatter);
    }

    public static String generateTransactionID(){
       return String.valueOf(UUID.randomUUID());
    }
}
