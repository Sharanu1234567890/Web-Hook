package com.example.webhook.delivery;



import com.example.webhook.client.ClientRepository;
import com.example.webhook.client.WebhookClient;
import com.example.webhook.event.WebhookEvent;
import com.example.webhook.log.DeliveryLog;
import com.example.webhook.log.DeliveryLogRepository;
import com.example.webhook.log.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryWorker {

    private final ClientRepository clientRepo;
    private final DeliveryLogRepository logRepo;
    private final HmacService hmacService;
    private final RetryScheduler retryScheduler;
    private final RestTemplate restTemplate;

    @KafkaListener(topics = "webhook-events")
    public void consume(WebhookEvent event) {
        List<WebhookClient> clients = clientRepo
                .findBySubscribedEventAndActive(event.getType(), true);

        for (WebhookClient client : clients) {
            attempt(client, event, 1);
        }
    }

    public void attempt(WebhookClient client, WebhookEvent event, int attemptNo) {
        DeliveryLog log = new DeliveryLog();
        log.setClientId(client.getId());
        log.setEventId(event.getId());
        log.setAttemptNumber(attemptNo);
        log.setAttemptedAt(Instant.now());

        try {
            String signature = hmacService.sign(event.getPayload(), client.getSecret());

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Webhook-Signature", signature);
            headers.set("X-Webhook-Event", event.getType().name());
            headers.setContentType(MediaType.APPLICATION_JSON);

            long start = System.currentTimeMillis();
            restTemplate.postForEntity(
                    client.getEndpointUrl(),
                    new HttpEntity<>(event.getPayload(), headers),
                    Void.class
            );
            long latency = System.currentTimeMillis() - start;

            log.setStatus(DeliveryStatus.SUCCESS);
            log.setLatencyMs(latency);

        } catch (Exception e) {
            log.setStatus(DeliveryStatus.FAILED);
            log.setErrorMessage(e.getMessage());

            if (attemptNo < 5) {
                retryScheduler.scheduleRetry(client, event, attemptNo);
            } else {
                log.setStatus(DeliveryStatus.DEAD);
            }
        }

        logRepo.save(log);
    }
}