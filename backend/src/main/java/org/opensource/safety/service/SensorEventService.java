package org.opensource.safety.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.opensource.safety.domain.AlertHistory;
import org.opensource.safety.domain.RiskAssessment;
import org.opensource.safety.domain.Senior;
import org.opensource.safety.domain.SensorEvent;
import org.opensource.safety.dto.AiAnalyzeRequest;
import org.opensource.safety.dto.AiAnalyzeResponse;
import org.opensource.safety.dto.SensorEventRequest;
import org.opensource.safety.repository.AlertHistoryRepository;
import org.opensource.safety.repository.RiskAssessmentRepository;
import org.opensource.safety.repository.SeniorRepository;
import org.opensource.safety.repository.SensorEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SensorEventService {
    private final SeniorRepository seniorRepository;
    private final SensorEventRepository sensorEventRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AlertHistoryRepository alertHistoryRepository;
    private final AiAnalysisClient aiAnalysisClient;

    public SensorEventService(
        SeniorRepository seniorRepository,
        SensorEventRepository sensorEventRepository,
        RiskAssessmentRepository riskAssessmentRepository,
        AlertHistoryRepository alertHistoryRepository,
        AiAnalysisClient aiAnalysisClient
    ) {
        this.seniorRepository = seniorRepository;
        this.sensorEventRepository = sensorEventRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.alertHistoryRepository = alertHistoryRepository;
        this.aiAnalysisClient = aiAnalysisClient;
    }

    @Transactional
    public RiskAssessment saveAndAnalyze(SensorEventRequest request) {
        Senior senior = seniorRepository.findById(request.seniorId())
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 독거노인 ID입니다."));

        SensorEvent event = new SensorEvent();
        event.setSenior(senior);
        event.setMotionDetected(request.motionDetected());
        event.setDoorOpened(request.doorOpened());
        event.setTemperature(request.temperature());
        event.setHumidity(request.humidity());
        event.setIlluminance(request.illuminance());
        event.setEventTime(request.eventTime() == null ? LocalDateTime.now() : request.eventTime());
        SensorEvent savedEvent = sensorEventRepository.save(event);

        LocalDateTime lastMotionAt = sensorEventRepository
            .findTopBySeniorIdAndMotionDetectedTrueAndEventTimeBeforeOrderByEventTimeDesc(
                senior.getId(),
                savedEvent.getEventTime()
            )
            .map(SensorEvent::getEventTime)
            .orElse(null);

        AiAnalyzeResponse aiResponse = aiAnalysisClient.analyze(new AiAnalyzeRequest(
            senior.getId(),
            savedEvent.isMotionDetected(),
            savedEvent.isDoorOpened(),
            savedEvent.getTemperature(),
            savedEvent.getHumidity(),
            savedEvent.getIlluminance(),
            savedEvent.getEventTime().toString(),
            lastMotionAt == null ? null : lastMotionAt.toString(),
            senior.getBaselineActiveStartHour(),
            senior.getBaselineActiveEndHour()
        ));

        RiskAssessment assessment = new RiskAssessment();
        assessment.setSenior(senior);
        assessment.setSensorEvent(savedEvent);
        assessment.setScore(aiResponse.score());
        assessment.setStatus(aiResponse.status());
        assessment.setReasons(String.join("\n", aiResponse.reasons()));
        RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);

        if (!"NORMAL".equals(savedAssessment.getStatus())) {
            createAlert(senior, savedAssessment);
        }
        return savedAssessment;
    }

    public List<SensorEvent> recentEvents(Long seniorId) {
        return sensorEventRepository.findTop50BySeniorIdOrderByEventTimeDesc(seniorId);
    }

    private void createAlert(Senior senior, RiskAssessment assessment) {
        AlertHistory alert = new AlertHistory();
        alert.setSenior(senior);
        alert.setRiskAssessment(assessment);
        alert.setLevel(assessment.getStatus());
        alert.setReceiver(senior.getGuardianName() + " / " + senior.getGuardianPhone());
        alert.setSent(true);
        String reasons = assessment.getReasons().lines().collect(Collectors.joining(", "));
        alert.setMessage("[%s] %s님의 위험 점수 %d점: %s".formatted(
            assessment.getStatus(),
            senior.getName(),
            assessment.getScore(),
            reasons
        ));
        alertHistoryRepository.save(alert);
    }
}
