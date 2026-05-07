package com.example.webhook.log;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, UUID> {
    Page<DeliveryLog> findByClientIdOrderByAttemptedAtDesc(UUID clientId, Pageable pageable);
}