package org.opensource.safety.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seniors")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Senior {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    private String address;
    private String guardianName;
    private String guardianPhone;
    private int baselineActiveStartHour = 7;
    private int baselineActiveEndHour = 22;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }
    public int getBaselineActiveStartHour() { return baselineActiveStartHour; }
    public void setBaselineActiveStartHour(int baselineActiveStartHour) { this.baselineActiveStartHour = baselineActiveStartHour; }
    public int getBaselineActiveEndHour() { return baselineActiveEndHour; }
    public void setBaselineActiveEndHour(int baselineActiveEndHour) { this.baselineActiveEndHour = baselineActiveEndHour; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
