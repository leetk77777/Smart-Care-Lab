package org.opensource.safety.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DashboardResponse(
    SeniorSummary senior,
    RiskSummary risk,
    SensorSummary latestSensor,
    List<SensorSummary> recentEvents,
    List<AlertSummary> alerts,
    List<ActivityPoint> activityChart
) {
    public record SeniorSummary(Long id, String name, int age, String address, String guardianName, String guardianPhone) {}
    public record RiskSummary(int score, String status, List<String> reasons, LocalDateTime assessedAt) {}
    public record SensorSummary(Long id, boolean motionDetected, boolean doorOpened, double temperature, double humidity, double illuminance, LocalDateTime eventTime) {}
    public record AlertSummary(Long id, String level, String message, String receiver, boolean sent, LocalDateTime createdAt) {}
    public record ActivityPoint(String label, int activity, double temperature, double humidity) {}
}
