package org.opensource.safety.service;

import java.util.List;
import org.opensource.safety.domain.Senior;
import org.opensource.safety.dto.SeniorRequest;
import org.opensource.safety.repository.SeniorRepository;
import org.springframework.stereotype.Service;

@Service
public class SeniorService {
    private final SeniorRepository seniorRepository;

    public SeniorService(SeniorRepository seniorRepository) {
        this.seniorRepository = seniorRepository;
    }

    public List<Senior> findAll() {
        return seniorRepository.findAll();
    }

    public Senior create(SeniorRequest request) {
        Senior senior = new Senior();
        senior.setName(request.name());
        senior.setAge(request.age());
        senior.setAddress(request.address());
        senior.setGuardianName(request.guardianName());
        senior.setGuardianPhone(request.guardianPhone());
        if (request.baselineActiveEndHour() > request.baselineActiveStartHour()) {
            senior.setBaselineActiveStartHour(request.baselineActiveStartHour());
            senior.setBaselineActiveEndHour(request.baselineActiveEndHour());
        }
        return seniorRepository.save(senior);
    }
}
