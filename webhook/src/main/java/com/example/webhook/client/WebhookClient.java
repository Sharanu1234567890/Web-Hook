package com.example.webhook.client;


import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class WebhookClient {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String endpointUrl;
    private String secret;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private EventType subscribedEvent;
}