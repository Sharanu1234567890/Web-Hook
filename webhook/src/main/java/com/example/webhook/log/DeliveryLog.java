package com.example.webhook.log;


import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class DeliveryLog {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID clientId;
    private UUID eventId;
    private int attemptNumber;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private Long latencyMs;
    private String errorMessage;
    private Instant attemptedAt;
}