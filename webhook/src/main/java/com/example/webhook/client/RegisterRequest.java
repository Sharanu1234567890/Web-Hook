package com.example.webhook.client;


import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String endpointUrl;
    private EventType eventType;
}