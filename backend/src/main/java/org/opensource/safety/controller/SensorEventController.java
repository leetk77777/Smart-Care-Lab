package org.opensource.safety.controller;

import java.util.List;
import org.opensource.safety.domain.RiskAssessment;
import org.opensource.safety.domain.SensorEvent;
import org.opensource.safety.dto.SensorEventRequest;
import org.opensource.safety.service.SensorEventService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor-events")
public class SensorEventController {
    private final SensorEventService sensorEventService;

    public SensorEventController(SensorEventService sensorEventService) {
        this.sensorEventService = sensorEventService;
    }

    @PostMapping
    public RiskAssessment create(@RequestBody SensorEventRequest request) {
        return sensorEventService.saveAndAnalyze(request);
    }

    @GetMapping("/senior/{seniorId}")
    public List<SensorEvent> recent(@PathVariable Long seniorId) {
        return sensorEventService.recentEvents(seniorId);
    }
}
