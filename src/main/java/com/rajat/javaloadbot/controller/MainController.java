package com.rajat.javaloadbot.controller;

import com.rajat.javaloadbot.GetAccount;
import com.rajat.javaloadbot.StartLoadbot;
import com.rajat.javaloadbot.TransactionCount;
import com.rajat.javaloadbot.DTO.TransactionCountResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    
    @Autowired
    StartLoadbot startLoadbot;

    @Autowired
    GetAccount getAccount;

    @Autowired
    TransactionCount transactionCount;

    @GetMapping("/")
    public void test() throws Exception {
        startLoadbot.test1();
    }

    @GetMapping("/sender")
    public void getSenderAccount() throws Exception {
        getAccount.getAccount();
    }

    @GetMapping("/start")
    public void startBot() throws Exception {
        startLoadbot.startLoadbot();
    }

    @GetMapping("/fund")
    public void preFundAccounts() throws Exception {
        startLoadbot.preFundAccounts();
    }

    @GetMapping("/multiple")
    public void startMultipleTransaction() throws Exception {
        startLoadbot.startMultipleTransaction();
    }

    @GetMapping("/total")
    public TransactionCountResponse getTransactionsCount() {
        return transactionCount.getTransactionsCount();
    }
}
