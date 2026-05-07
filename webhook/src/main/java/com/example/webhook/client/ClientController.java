package com.example.webhook.client;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRepository clientRepo;

    @PostMapping
    public ResponseEntity<WebhookClient> register(@RequestBody RegisterRequest req) {
        WebhookClient client = new WebhookClient();
        client.setName(req.getName());
        client.setEndpointUrl(req.getEndpointUrl());
        client.setSecret(UUID.randomUUID().toString());
        client.setSubscribedEvent(req.getEventType());
        client.setActive(true);
        return ResponseEntity.ok(clientRepo.save(client));
    }

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        return ResponseEntity.ok(clientRepo.findAll());
    }
}