package com.example.webhook.delivery;

import com.example.webhook.client.WebhookClient;
import com.example.webhook.event.WebhookEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RetryScheduler {

    private final TaskScheduler taskScheduler;
    private final ApplicationContext context;

    public void scheduleRetry(WebhookClient client, WebhookEvent event, int lastAttempt) {
        long delaySeconds = (long) Math.pow(2, lastAttempt) * 60;
        taskScheduler.schedule(() -> {
            DeliveryWorker worker = context.getBean(DeliveryWorker.class);
            worker.attempt(client, event, lastAttempt + 1);
        }, Instant.now().plusSeconds(delaySeconds));
    }
}