package com.example.webhook.log;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final DeliveryLogRepository logRepo;

    @GetMapping("/{clientId}")
    public Page<DeliveryLog> getLogs(
            @PathVariable UUID clientId,
            @RequestParam(defaultValue = "0") int page) {
        return logRepo.findByClientIdOrderByAttemptedAtDesc(
                clientId, PageRequest.of(page, 20));
    }
}