package com.example.webhook.event;


import com.example.webhook.client.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventPublisher eventPublisher;

    @PostMapping("/trigger")
    public String trigger(@RequestBody Map<String, String> body) throws Exception {
        EventType type = EventType.valueOf(body.get("type"));
        eventPublisher.publish(type, body);
        return "Event published!";
    }
}