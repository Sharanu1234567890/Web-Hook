package com.example.webhook.client;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<WebhookClient, UUID> {
    List<WebhookClient> findBySubscribedEventAndActive(EventType event, boolean active);
}