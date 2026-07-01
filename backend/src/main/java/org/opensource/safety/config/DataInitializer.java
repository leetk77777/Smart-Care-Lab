package org.opensource.safety.config;

import org.opensource.safety.domain.Senior;
import org.opensource.safety.repository.SeniorRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    private final SeniorRepository seniorRepository;

    public DataInitializer(SeniorRepository seniorRepository) {
        this.seniorRepository = seniorRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (seniorRepository.count() > 0) {
            return;
        }
        Senior senior = new Senior();
        senior.setName("김영희");
        senior.setAge(78);
        senior.setAddress("서울시 중구");
        senior.setGuardianName("김민수");
        senior.setGuardianPhone("010-1234-5678");
        senior.setBaselineActiveStartHour(7);
        senior.setBaselineActiveEndHour(22);
        seniorRepository.save(senior);
    }
}
