package com.rajat.javaloadbot.controller;

import com.rajat.javaloadbot.StartLoadbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    
    @Autowired
    StartLoadbot startLoadbot;

    @GetMapping("/")
    public void test() throws Exception {
        startLoadbot.test1();
    }

    @GetMapping("/start")
    public void startBot() throws Exception {
        startLoadbot.startLoadbot();
    }

}
