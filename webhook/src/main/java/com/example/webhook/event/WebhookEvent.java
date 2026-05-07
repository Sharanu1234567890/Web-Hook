package com.example.webhook.event;


import com.example.webhook.client.EventType;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class WebhookEvent {
    private UUID id;
    private EventType type;
    private String payload;
    private Instant createdAt;
}