package com.rajat.javaloadbot;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rajat.javaloadbot.DTO.TransactionCountResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

@Service
public class TransactionCount {
    
    static Web3j web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));//RPC SERVER
    
    @Autowired
    SendTransaction sendTransaction;

    public TransactionCountResponse getTransactionsCount() {
        try {
            int total=0;
            Map<String, Integer> map = new LinkedHashMap<>();
            for (int i = 0; i < 1000; i++) {
                String address = sendTransaction.getRecipientAddress(i);
                // Get the latest nonce of current account
                EthGetTransactionCount ethGetTransactionCount = web3
                        .ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
                BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                System.out.println("Account: "+address+"  Nonce: "+nonce.toString());
                total += nonce.intValue();
                map.put(address, nonce.intValue());
            }
            System.out.println("TOTAL TRANSACTIONS: "+total);
            TransactionCountResponse res = new TransactionCountResponse("success", map, total);
            return res;
            
        } catch (Exception e) {
            System.out.println("Error in getTransactionsCount(): "+e);
            return new TransactionCountResponse(e.getMessage(), null, 0);
        }
    }
}
