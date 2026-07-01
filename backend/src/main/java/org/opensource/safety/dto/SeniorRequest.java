package org.opensource.safety.dto;

public record SeniorRequest(
    String name,
    int age,
    String address,
    String guardianName,
    String guardianPhone,
    int baselineActiveStartHour,
    int baselineActiveEndHour
) {
}
