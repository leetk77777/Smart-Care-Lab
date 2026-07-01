package org.opensource.safety.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_events")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SensorEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Senior senior;
    private boolean motionDetected;
    private boolean doorOpened;
    private double temperature;
    private double humidity;
    private double illuminance;
    private LocalDateTime eventTime;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Senior getSenior() { return senior; }
    public void setSenior(Senior senior) { this.senior = senior; }
    public boolean isMotionDetected() { return motionDetected; }
    public void setMotionDetected(boolean motionDetected) { this.motionDetected = motionDetected; }
    public boolean isDoorOpened() { return doorOpened; }
    public void setDoorOpened(boolean doorOpened) { this.doorOpened = doorOpened; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public double getIlluminance() { return illuminance; }
    public void setIlluminance(double illuminance) { this.illuminance = illuminance; }
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
