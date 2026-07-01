package org.opensource.safety.controller;

import java.util.List;
import org.opensource.safety.domain.AlertHistory;
import org.opensource.safety.repository.AlertHistoryRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertHistoryRepository alertHistoryRepository;

    public AlertController(AlertHistoryRepository alertHistoryRepository) {
        this.alertHistoryRepository = alertHistoryRepository;
    }

    @GetMapping("/senior/{seniorId}")
    public List<AlertHistory> recent(@PathVariable Long seniorId) {
        return alertHistoryRepository.findTop20BySeniorIdOrderByCreatedAtDesc(seniorId);
    }
}
