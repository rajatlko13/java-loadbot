package com.rajat.javaloadbot.DTO;

import java.util.Map;

public class TransactionCountResponse {
    
    String message;

    Map<String, Integer> map;

    int total;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public TransactionCountResponse(String message, Map<String, Integer> map, int total) {
        this.message = message;
        this.map = map;
        this.total = total;
    }

}
