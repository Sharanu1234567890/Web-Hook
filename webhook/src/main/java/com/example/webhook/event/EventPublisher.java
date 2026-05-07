package com.example.webhook.event;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.webhook.client.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, WebhookEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(EventType type, Object data) throws Exception {
        WebhookEvent event = new WebhookEvent();
        event.setId(UUID.randomUUID());
        event.setType(type);
        event.setPayload(objectMapper.writeValueAsString(data));
        event.setCreatedAt(Instant.now());

        kafkaTemplate.send("webhook-events", event.getId().toString(), event);
    }
}