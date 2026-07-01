package org.opensource.safety.service;

import java.time.LocalDateTime;
import java.util.List;
import org.opensource.safety.dto.SensorEventRequest;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    private final SensorEventService sensorEventService;

    public DemoService(SensorEventService sensorEventService) {
        this.sensorEventService = sensorEventService;
    }

    public void runScenario(Long seniorId, String scenario) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        List<SensorEventRequest> events = switch (scenario) {
            case "danger" -> List.of(
                new SensorEventRequest(seniorId, true, false, 25.0, 47.0, 120.0, now.withHour(22).minusDays(1)),
                new SensorEventRequest(seniorId, false, true, 34.0, 82.0, 20.0, now.withHour(3).withMinute(20))
            );
            case "caution" -> List.of(
                new SensorEventRequest(seniorId, false, false, 30.5, 50.0, 260.0, now.withHour(10).withMinute(10)),
                new SensorEventRequest(seniorId, true, false, 30.0, 48.0, 280.0, now.withHour(23).withMinute(20))
            );
            default -> List.of(
                new SensorEventRequest(seniorId, true, false, 24.0, 45.0, 320.0, now.minusMinutes(40)),
                new SensorEventRequest(seniorId, true, false, 24.3, 46.0, 360.0, now.minusMinutes(15)),
                new SensorEventRequest(seniorId, true, false, 24.6, 44.0, 390.0, now)
            );
        };
        events.forEach(sensorEventService::saveAndAnalyze);
    }
}
