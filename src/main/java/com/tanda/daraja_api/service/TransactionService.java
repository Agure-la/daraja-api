package com.tanda.daraja_api.service;

import com.tanda.daraja_api.entity.PendingPayment;
import com.tanda.daraja_api.entity.Transaction;
import com.tanda.daraja_api.models.AcknowledgeResponse;
import com.tanda.daraja_api.repository.PendingPaymentRepository;
import com.tanda.daraja_api.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    B2CService b2CService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    PendingPaymentRepository pendingPaymentRepository;


    public AcknowledgeResponse saveTransaction(Transaction transaction){
        if (transactionExist(transaction.getTransactionId())){
            return new AcknowledgeResponse("Transaction Exist", false);
        }

        transactionRepository.save(transaction);
        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setTransactionId(transaction.getTransactionId());
        pendingPayment.setAmount(transaction.getAmount());
        pendingPaymentRepository.save(pendingPayment);

        Optional<Transaction> savedTransaction = transactionRepository.findByTransactionId(transaction.getTransactionId());
        if (savedTransaction.isPresent()){
            b2CService.initiateB2CTransaction(transaction);
        }

        return new AcknowledgeResponse("Success", true);
    }

    public boolean transactionExist(String transactionId){
        Optional<Transaction> optionalTransaction = transactionRepository.findByTransactionId(transactionId);
        return optionalTransaction.isPresent();
    }

}
