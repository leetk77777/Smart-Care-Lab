package org.opensource.safety.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RiskAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Senior senior;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_event_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private SensorEvent sensorEvent;
    private int score;
    private String status;
    @Column(length = 2000)
    private String reasons;
    private LocalDateTime assessedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Senior getSenior() { return senior; }
    public void setSenior(Senior senior) { this.senior = senior; }
    public SensorEvent getSensorEvent() { return sensorEvent; }
    public void setSensorEvent(SensorEvent sensorEvent) { this.sensorEvent = sensorEvent; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReasons() { return reasons; }
    public void setReasons(String reasons) { this.reasons = reasons; }
    public LocalDateTime getAssessedAt() { return assessedAt; }
}
