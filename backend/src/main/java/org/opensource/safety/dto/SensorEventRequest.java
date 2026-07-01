package org.opensource.safety.dto;

import java.time.LocalDateTime;

public record SensorEventRequest(
    Long seniorId,
    boolean motionDetected,
    boolean doorOpened,
    double temperature,
    double humidity,
    double illuminance,
    LocalDateTime eventTime
) {
}
