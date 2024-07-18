package com.tanda.daraja_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanda.daraja_api.config.MpesaConfiguration;
import com.tanda.daraja_api.entity.AuthToken;
import com.tanda.daraja_api.entity.B2CResponseEntity;
import com.tanda.daraja_api.entity.Transaction;
import com.tanda.daraja_api.repository.AuthTokenRepository;
import com.tanda.daraja_api.repository.B2CResponseEntityRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class B2CService {

    @Autowired
    AuthTokenRepository tokenRepository;

    private final MpesaConfiguration mpesaConfiguration;

    private final OkHttpClient client;

    private final ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(B2CService.class);

    @Value("${security.credential}")
    String securityCredential;

    @Autowired
    private B2CResponseEntityRepository b2CResponseEntityRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String CHARACTERS = "0123456789abcdef";

    private static final Random RANDOM = new Random();

    public String generateAccessToken() {
        String token;

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
                .method("GET", null)
                .addHeader("Authorization", "Basic a0xIZ080RTgzQ3cxUjNnQ3ZwSEVhS0gwRXFrQkduSWgxdzlyY01XSGk4c1lkVzR2OlM1eWlrRWRTVkg3TW9IT0tDZ3VSOGJoRGwwS3Nkbk1CcEM0bjdGa2FIR1RjWFd4dFNLbWI4blk2WG1vb2VZbVg=")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            // Parse the response body string into a JSONObject
            JSONObject json = (JSONObject) JSONValue.parse(responseBody);
           // Retrieve the access token from the parsed JSON object
            token = (String) json.get("access_token");
            return token;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String checkToken(String name){
        Optional<AuthToken> optionalAuthToken = tokenRepository.findByName(name);
        String token;
        AuthToken authToken = new AuthToken();
        if (optionalAuthToken.isEmpty()){
          token = generateAccessToken();
          authToken.setName(name);
          authToken.setToken(token);
        }
        else {
            long updatedAtTimestamp = optionalAuthToken.get().getUpdatedAt().getTime();

          // Get current time in milliseconds
            long currentTimeMillis = System.currentTimeMillis();

           // Calculate minutes difference
            long minutesDifference = (currentTimeMillis - updatedAtTimestamp) / (1000 * 60);
            if (minutesDifference > 59){
                token = generateAccessToken();
                authToken.setToken(token);
            }
            else {
                token = optionalAuthToken.get().getToken();
            }
        }
        tokenRepository.save(authToken);
        return token;
    }

    public void initiateB2CTransaction(Transaction transaction){
        String accessToken = checkToken("B2C");

        System.out.println("REQUEST " + transaction.getPhone());
        System.out.println("TOKEEEEEN " + accessToken);
        JSONObject json = new JSONObject();
        String transactionId = transaction.getTransactionId();
        String phoneNumber = transaction.getPhone();
        json.put("OriginatorConversationID", transactionId);
        json.put("InitiatorName", "testapi");
        json.put("SecurityCredential", securityCredential);
        json.put("CommandID", "SalaryPayment");
        json.put("Amount", transaction.getAmount());
        json.put("PartyA", "600983");
        json.put("PartyB", phoneNumber);
        json.put("Remarks", "Test remarks");
        json.put("QueueTimeOutURL", "https://webhook.site/a4ff5d34-ee6d-45ea-8a12-618b617f363b");
        json.put("ResultURL", "https://webhook.site/a4ff5d34-ee6d-45ea-8a12-618b617f363b");
        json.put("occasion", "null");

        System.out.println("OBJECT " + json);
// Serialize JSONObject to JSON string
        String jsonString = json.toJSONString();
        System.out.println("CONVERTED " + jsonString);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonString);
        Request request = new Request.Builder()

                .url("https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", "incap_ses_1021_2742146=MUUKQCM9g3K6ixbaMFIrDh7Hl2YAAAAA74TtN6e/q6B88h0S13f2/w==; visid_incap_2742146=Mt9J82EdSAO0DXHDKD0rDx7Hl2YAAAAAQUIPAAAAAAAyUT1agNeQ9/vfMl4xSRC5")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            // Parse the response body string into a JSONObject
            JSONObject jsons = (JSONObject) JSONValue.parse(responseBody);
            String conversationID = (String) jsons.get("ConversationID");
            String originatorConversationID = (String) jsons.get("OriginatorConversationID");
            String responseDescription = (String) jsons.get("ResponseDescription");

            B2CResponseEntity responseEntity = new B2CResponseEntity();
            responseEntity.setConversationID(conversationID);
            responseEntity.setOriginatorConversationID(originatorConversationID);
            responseEntity.setResponseDescription(responseDescription);
            b2CResponseEntityRepository.save(responseEntity);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateRandomString() {
        return new StringBuilder()
                .append(generateRandomSegment(8)).append("-")
                .append(generateRandomSegment(4)).append("-")
                .append(generateRandomSegment(4)).append("-")
                .append(generateRandomSegment(4)).append("-")
                .append(generateRandomSegment(12))
                .toString();
    }

    private static String generateRandomSegment(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public void processMpesaResponse(String response) {
        try {
            // Parse the response
            JSONObject jsonResponse = (JSONObject) JSONValue.parse(response);
            System.out.println("RESPONSE " + jsonResponse);

            String responseCode = (String) jsonResponse.get("ResponseCode");

            // Check if the response code indicates success
            if ("0".equals(responseCode)) {
                // Extract necessary fields
                String conversationID = (String) jsonResponse.get("ConversationID");
                String originatorConversationID = (String) jsonResponse.get("OriginatorConversationID");
                String responseDescription = (String) jsonResponse.get("ResponseDescription");

                // Log the successful response
                System.out.println("Mpesa Response Successful: " + responseDescription);

                storeMpesaResponse(conversationID, originatorConversationID, responseDescription, "Success");

                // Send the result back to CPS
                sendResultToCPS(conversationID, "Success", originatorConversationID);

            } else {
                // Log the failure response
                System.err.println("Mpesa Response Failed: " + response);

                // Handle failure scenario
                sendResultToCPS(null, "Failure", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResultToCPS(null, "Failure", null);
        }
    }

    private void storeMpesaResponse(String conversationID, String originatorConversationID, String responseDescription, String status) {
        B2CResponseEntity b2CResponseEntity = new B2CResponseEntity();
        b2CResponseEntity.setConversationID(conversationID);
        b2CResponseEntity.setOriginatorConversationID(originatorConversationID);
        b2CResponseEntity.setResponseDescription(responseDescription);
       // b2CResponseEntity.setStatus(status);
        b2CResponseEntityRepository.save(b2CResponseEntity);
    }

    private void sendResultToCPS(String transactionId, String status, String mpesaRef) {
        String resultPayload = String.format("{\"transactionId\":\"%s\",\"status\":\"%s\",\"mpesaRef\":\"%s\"}", transactionId, status, mpesaRef);
        kafkaTemplate.send("cpsResultTopic", resultPayload);
    }

    public void handleMpesaCallback(Object callback) {
        try {
            JSONObject jsonResponse = (JSONObject) JSONValue.parse(callback.toString());
            String originatorConversationID = (String) jsonResponse.get("OriginatorConversationID");
            String conversationId = (String) jsonResponse.get("conversationID");
            String resultCode = (String) jsonResponse.get("ResultCode");

            String status;
            if ("0".equals(resultCode)) {
                status = "Success";
                Optional<B2CResponseEntity> existingTransaction = b2CResponseEntityRepository.findByOriginatorConversationIDAndConversationID(originatorConversationID, conversationId);
                if(existingTransaction.isEmpty()){
                    throw new RuntimeException("Transaction does not exist");
                }
                existingTransaction.get().setResultDesc((String) jsonResponse.get("ResultDesc"));
                existingTransaction.get().setTransactionId((String) jsonResponse.get("TransactionID"));
                String test = (String) jsonResponse.get("ResultParameter");
                System.out.println("TESSSSSSSSSSSSSSSSSSSSSSTTTT " + test);
                //existingTransaction.get().setTransactionAmount();
            } else {
                status = "Failure";
            }

            // Update MariaDB with the final status
            updateTransactionStatus(originatorConversationID, status);

            // Send the final result to CPS
            sendResultToCPS(null, status, originatorConversationID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTransactionStatus(String originatorConversationID, String status) {
        B2CResponseEntity b2CResponseEntity = b2CResponseEntityRepository.findByOriginatorConversationID(originatorConversationID);
        if (b2CResponseEntity != null) {
           // b2CResponseEntity.setStatus(status);
            b2CResponseEntityRepository.save(b2CResponseEntity);
        }
    }
}
