package org.opensource.safety.dto;

public record AiAnalyzeRequest(
    Long seniorId,
    boolean motionDetected,
    boolean doorOpened,
    double temperature,
    double humidity,
    double illuminance,
    String eventTime,
    String lastMotionAt,
    int baselineActiveStartHour,
    int baselineActiveEndHour
) {
}
