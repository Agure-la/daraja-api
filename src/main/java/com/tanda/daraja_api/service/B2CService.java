package com.tanda.daraja_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanda.daraja_api.entity.AuthToken;
import com.tanda.daraja_api.entity.B2CRequestEntity;
import com.tanda.daraja_api.messaging.KafkaProducer;
import com.tanda.daraja_api.models.request.GwRequest;
import com.tanda.daraja_api.repository.AuthTokenRepository;
import com.tanda.daraja_api.repository.B2CResponseEntityRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class B2CService {

    @Autowired
    AuthTokenRepository tokenRepository;

    private final OkHttpClient client;

    private final ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(B2CService.class);

    @Value("${security.credential}")
    String securityCredential;

    @Autowired
    private B2CResponseEntityRepository b2CResponseEntityRepository;

    @Autowired
    KafkaProducer kafkaProducer;

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

    public String initiateB2CTransaction(GwRequest gwRequest){
        String accessToken = checkToken("B2C");

        JSONObject json = new JSONObject();
        String phoneNumber = gwRequest.getMobileNumber();
        json.put("OriginatorConversationID", generateRandomString());
        json.put("InitiatorName", "testapi");
        json.put("SecurityCredential", securityCredential);
        json.put("CommandID", "SalaryPayment");
        json.put("Amount", gwRequest.getAmount());
        json.put("PartyA", "600983");
        json.put("PartyB", phoneNumber);
        json.put("Remarks", "Test remarks");
        json.put("QueueTimeOutURL", "https://www.tanda.africa/payment/callback");
        json.put("ResultURL", "https://www.tanda.africa/payment/callback");
        json.put("occasion", "null");

       // Serialize JSONObject to JSON string
        String jsonString = json.toJSONString();
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

            B2CRequestEntity responseEntity = new B2CRequestEntity();
            responseEntity.setConversationID(conversationID);
            responseEntity.setOriginatorConversationID(originatorConversationID);
            responseEntity.setResponseDescription(responseDescription);
            responseEntity.setCallBackUrl(gwRequest.getCallBackUrl());
            responseEntity.setTransactionAmount(gwRequest.getAmount());
            responseEntity.setUuid(gwRequest.getUuid());
            b2CResponseEntityRepository.save(responseEntity);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Success";
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
            String responseCode = (String) jsonResponse.get("ResponseCode");

            // Check if the response code indicates success
            if ("0".equals(responseCode)) {
                // Extract necessary fields
                String conversationID = (String) jsonResponse.get("ConversationID");
                String originatorConversationID = (String) jsonResponse.get("OriginatorConversationID");
                String responseDescription = (String) jsonResponse.get("ResponseDescription");

                // Log the successful response
                storeMpesaResponse(conversationID, originatorConversationID, responseDescription, 1);

                // Send the result back to CPS
                sendResultToCPS(conversationID, 1, originatorConversationID);

            } else {
                // Handle failure scenario
                sendResultToCPS(null, 2, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResultToCPS(null, 2, null);
        }
    }

    private void storeMpesaResponse(String conversationID, String originatorConversationID, String responseDescription, int status) {
        B2CRequestEntity b2CRequestEntity = new B2CRequestEntity();
        b2CRequestEntity.setConversationID(conversationID);
        b2CRequestEntity.setOriginatorConversationID(originatorConversationID);
        b2CRequestEntity.setResponseDescription(responseDescription);
        b2CRequestEntity.setStatus(status);
        b2CResponseEntityRepository.save(b2CRequestEntity);
    }

    private void sendResultToCPS(String transactionId, int status, String mpesaRef) {
        String resultPayload = String.format("{\"transactionId\":\"%s\",\"status\":\"%d\",\"mpesaRef\":\"%s\"}", transactionId, status, mpesaRef);
        kafkaProducer.sendMessage("cpsResultTopic", resultPayload);
    }

    public String handleMpesaCallback(Object callback) {
        try {
            JSONObject jsonResponse = (JSONObject) JSONValue.parse(callback.toString());
            JSONObject result = (JSONObject) jsonResponse.get("Result");

            if (result != null) {
                String originatorConversationID = (String) result.get("OriginatorConversationID");
                String conversationId = (String) result.get("ConversationID");
                int resultCode = (int) result.get("ResultCode");
                String resultDes = (String) result.get("ResultDesc");
                String transactionId = (String) result.get("TransactionID");

                Optional<B2CRequestEntity> existingTransaction = b2CResponseEntityRepository.findByOriginatorConversationIDAndConversationID(originatorConversationID, conversationId);
                if (existingTransaction.isEmpty()) {
                    throw new RuntimeException("Transaction does not exist");
                }
                if (resultCode == 0) {
                    existingTransaction.get().setResultDesc((String) jsonResponse.get("ResultDesc"));
                    existingTransaction.get().setTransactionId((String) jsonResponse.get("TransactionID"));
                    existingTransaction.get().setStatus(1);
                    existingTransaction.get().setResultDesc(resultDes);
                    JSONObject resultParameters = (JSONObject) result.get("ResultParameters");
                    if (resultParameters != null) {
                        JSONArray resultParameterArray = (JSONArray) resultParameters.get("ResultParameter");
                        if (resultParameterArray != null) {
                            for (Object param : resultParameterArray) {
                                JSONObject paramObject = (JSONObject) param;
                                String key = (String) paramObject.get("Key");
                                Object value = paramObject.get("Value");
                                if ("TransactionAmount".equals(key)) {
                                    existingTransaction.get().setTransactionAmount((float) Double.parseDouble(value.toString()));
                                } else if ("ReceiverPartyPublicName".equals(key)) {
                                    existingTransaction.get().setReceiverPartyPublicName(value.toString());
                                } else if ("TransactionCompletedDateTime".equals(key)) {
                                    existingTransaction.get().setTransactionCompletedDateTime(value.toString());
                                } else if ("TransactionReceipt".equals(key)) {
                                    existingTransaction.get().setTransactionId(value.toString());
                                }

                            }
                        }
                    }

                } else {
                    existingTransaction.get().setStatus(2);
                    existingTransaction.get().setResultCode(resultCode);
                    existingTransaction.get().setResultDesc(resultDes);
                    existingTransaction.get().setTransactionId(transactionId);
                }
                b2CResponseEntityRepository.save(existingTransaction.get());

                // Send the final result to CPS
                sendResultToCPS(existingTransaction.get().getTransactionId(), existingTransaction.get().getStatus(), originatorConversationID);
            }
            } catch(Exception e){
                e.printStackTrace();
        }
        return "Success";
    }
}
