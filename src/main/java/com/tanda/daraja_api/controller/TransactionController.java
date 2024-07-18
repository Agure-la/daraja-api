package com.tanda.daraja_api.controller;

import com.tanda.daraja_api.entity.Transaction;
import com.tanda.daraja_api.enums.PaymentStatus;
import com.tanda.daraja_api.models.AcknowledgeResponse;
import com.tanda.daraja_api.models.request.GwRequest;
import com.tanda.daraja_api.service.TransactionService;
import com.tanda.daraja_api.utils.HelperUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public AcknowledgeResponse processTransaction(@RequestBody GwRequest gwRequest){
        System.out.println("CALLLLLED");
        System.out.println("WWW REQUEST " + gwRequest);
        Transaction transaction = new Transaction();
        transaction.setTransactionId(HelperUtility.generateTransactionID());
        transaction.setStatus(PaymentStatus.PENDING);
        transaction.setAmount(gwRequest.getAmount());
        transaction.setPhone(gwRequest.getMobileNumber());
        return transactionService.saveTransaction(transaction);
    }

    @PostMapping("/callback")
    public void C2BCallback(){

    }
}
