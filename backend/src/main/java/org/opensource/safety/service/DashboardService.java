package org.opensource.safety.service;

import java.util.Comparator;
import java.util.List;
import org.opensource.safety.domain.AlertHistory;
import org.opensource.safety.domain.RiskAssessment;
import org.opensource.safety.domain.Senior;
import org.opensource.safety.domain.SensorEvent;
import org.opensource.safety.dto.DashboardResponse;
import org.opensource.safety.repository.AlertHistoryRepository;
import org.opensource.safety.repository.RiskAssessmentRepository;
import org.opensource.safety.repository.SeniorRepository;
import org.opensource.safety.repository.SensorEventRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final SeniorRepository seniorRepository;
    private final SensorEventRepository sensorEventRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AlertHistoryRepository alertHistoryRepository;

    public DashboardService(SeniorRepository seniorRepository, SensorEventRepository sensorEventRepository, RiskAssessmentRepository riskAssessmentRepository, AlertHistoryRepository alertHistoryRepository) {
        this.seniorRepository = seniorRepository;
        this.sensorEventRepository = sensorEventRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.alertHistoryRepository = alertHistoryRepository;
    }

    public DashboardResponse getDashboard(Long seniorId) {
        Senior senior = seniorRepository.findById(seniorId)
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 독거노인 ID입니다."));
        List<SensorEvent> events = sensorEventRepository.findTop50BySeniorIdOrderByEventTimeDesc(seniorId);
        RiskAssessment risk = riskAssessmentRepository.findTopBySeniorIdOrderByAssessedAtDesc(seniorId).orElse(null);
        List<AlertHistory> alerts = alertHistoryRepository.findTop20BySeniorIdOrderByCreatedAtDesc(seniorId);

        return new DashboardResponse(
            new DashboardResponse.SeniorSummary(senior.getId(), senior.getName(), senior.getAge(), senior.getAddress(), senior.getGuardianName(), senior.getGuardianPhone()),
            risk == null
                ? new DashboardResponse.RiskSummary(0, "NORMAL", List.of("아직 분석된 센서 이벤트가 없습니다."), null)
                : new DashboardResponse.RiskSummary(risk.getScore(), risk.getStatus(), risk.getReasons().lines().toList(), risk.getAssessedAt()),
            events.stream().findFirst().map(this::toSensorSummary).orElse(null),
            events.stream().limit(10).map(this::toSensorSummary).toList(),
            alerts.stream().map(this::toAlertSummary).toList(),
            events.stream()
                .sorted(Comparator.comparing(SensorEvent::getEventTime))
                .skip(Math.max(0, events.size() - 12))
                .map(event -> new DashboardResponse.ActivityPoint(
                    "%02d:%02d".formatted(event.getEventTime().getHour(), event.getEventTime().getMinute()),
                    event.isMotionDetected() ? 1 : 0,
                    event.getTemperature(),
                    event.getHumidity()
                ))
                .toList()
        );
    }

    private DashboardResponse.SensorSummary toSensorSummary(SensorEvent event) {
        return new DashboardResponse.SensorSummary(
            event.getId(),
            event.isMotionDetected(),
            event.isDoorOpened(),
            event.getTemperature(),
            event.getHumidity(),
            event.getIlluminance(),
            event.getEventTime()
        );
    }

    private DashboardResponse.AlertSummary toAlertSummary(AlertHistory alert) {
        return new DashboardResponse.AlertSummary(
            alert.getId(),
            alert.getLevel(),
            alert.getMessage(),
            alert.getReceiver(),
            alert.isSent(),
            alert.getCreatedAt()
        );
    }
}
