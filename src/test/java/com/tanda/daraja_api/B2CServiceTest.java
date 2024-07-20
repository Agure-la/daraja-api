package com.tanda.daraja_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanda.daraja_api.entity.AuthToken;
import com.tanda.daraja_api.entity.B2CRequestEntity;
import com.tanda.daraja_api.messaging.KafkaProducer;
import com.tanda.daraja_api.models.request.GwRequest;
import com.tanda.daraja_api.repository.AuthTokenRepository;
import com.tanda.daraja_api.repository.B2CResponseEntityRepository;
import com.tanda.daraja_api.service.B2CService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class B2CServiceTest {

    @Mock
    private AuthTokenRepository tokenRepository;

    @Mock
    private B2CResponseEntityRepository b2CResponseEntityRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private OkHttpClient client;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private B2CService b2CService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckToken_NewTokenGenerated_WhenTokenNotFound() {
        String name = "testUser";
        when(tokenRepository.findByName(name)).thenReturn(Optional.empty());

        String generatedToken = "newToken";
        B2CService spyService = spy(b2CService);
        doReturn(generatedToken).when(spyService).generateAccessToken();

        String result = spyService.checkToken(name);

        assertEquals(generatedToken, result);
        verify(tokenRepository).save(any(AuthToken.class));
    }

    @Test
    public void testCheckToken_ExistingTokenUsed_WhenTokenIsValid() {
        String name = "testUser";
        AuthToken existingToken = new AuthToken();
        existingToken.setName(name);
        existingToken.setToken("existingToken");
        existingToken.setUpdatedAt(new Date(System.currentTimeMillis() - (30 * 60 * 1000)));
        when(tokenRepository.findByName(name)).thenReturn(Optional.of(existingToken));

        String result = b2CService.checkToken(name);
        assertEquals("existingToken", result);
    }

    @Test
    public void testCheckToken_NewTokenGenerated_WhenTokenIsExpired() {
        String name = "testUser";
        AuthToken existingToken = new AuthToken();
        existingToken.setName(name);
        existingToken.setToken("existingToken");
        existingToken.setUpdatedAt(new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000))); // 2 hours ago
        when(tokenRepository.findByName(name)).thenReturn(Optional.of(existingToken));

        String generatedToken = "newToken";
        B2CService spyService = spy(b2CService);
        doReturn(generatedToken).when(spyService).generateAccessToken();

        String result = spyService.checkToken(name);

        assertEquals(generatedToken, result);
        verify(tokenRepository).save(any(AuthToken.class));
    }

    @Test
    public void testInitiateB2CTransaction() throws IOException {
        GwRequest gwRequest = new GwRequest("uuid", 1000, "123456789", "http://callback.url");
        gwRequest.setMobileNumber("123456789");
        gwRequest.setAmount(1000);
        gwRequest.setCallBackUrl("http://callback.url");
        gwRequest.setUuid("uuid");

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"ConversationID\":\"convId\",\"OriginatorConversationID\":\"origConvId\",\"ResponseDescription\":\"Success\"}"));

        String baseUrl = mockWebServer.url("/").toString();

        String result = b2CService.initiateB2CTransaction(gwRequest);
        assertEquals("Success", result);
        verify(b2CResponseEntityRepository).save(any(B2CRequestEntity.class));

        mockWebServer.shutdown();
    }

    @Test
    public void testProcessMpesaResponse_Success() {
        String response = "{\"ResponseCode\":\"0\",\"ConversationID\":\"convId\",\"OriginatorConversationID\":\"origConvId\",\"ResponseDescription\":\"Success\"}";

        b2CService.processMpesaResponse(response);

        verify(b2CResponseEntityRepository).save(any(B2CRequestEntity.class));
        verify(kafkaProducer).sendMessage(eq("cpsResultTopic"), anyString());
    }

    @Test
    public void testHandleMpesaCallback() {
        String callback = "{\"Result\":{\"OriginatorConversationID\":\"origConvId\",\"ConversationID\":\"convId\",\"ResultCode\":0,\"ResultDesc\":\"Success\",\"ResultParameters\":{\"ResultParameter\":[{\"Key\":\"TransactionAmount\",\"Value\":\"100.0\"}]}}}";

        Optional<B2CRequestEntity> existingEntity = Optional.of(new B2CRequestEntity());
        when(b2CResponseEntityRepository.findByOriginatorConversationIDAndConversationID("origConvId", "convId")).thenReturn(existingEntity);

        String result = b2CService.handleMpesaCallback(callback);

        assertEquals("Success", result);
        verify(b2CResponseEntityRepository).save(any(B2CRequestEntity.class));
        verify(kafkaProducer).sendMessage(eq("cpsResultTopic"), anyString());
    }
}
