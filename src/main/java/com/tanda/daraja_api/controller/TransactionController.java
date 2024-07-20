package com.tanda.daraja_api.controller;

import com.tanda.daraja_api.models.request.GwRequest;
import com.tanda.daraja_api.service.B2CService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/payment")
public class TransactionController {

    @Autowired
    B2CService b2CService;

    @PostMapping("/transaction")
    public ResponseEntity<String> initiateB2CTransaction(@Valid @RequestBody GwRequest gwRequest) {
        try {
            String response = b2CService.initiateB2CTransaction(gwRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to initiate B2C transaction");
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleMpesaCallback(@RequestBody Object callback) {
        try {
            String response = b2CService.handleMpesaCallback(callback);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to handle Mpesa callback");
        }
    }
}
