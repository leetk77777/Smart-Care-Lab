package org.opensource.safety.repository;

import java.util.List;
import java.util.Optional;
import org.opensource.safety.domain.SensorEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorEventRepository extends JpaRepository<SensorEvent, Long> {
    List<SensorEvent> findTop50BySeniorIdOrderByEventTimeDesc(Long seniorId);
    Optional<SensorEvent> findTopBySeniorIdAndMotionDetectedTrueAndEventTimeBeforeOrderByEventTimeDesc(Long seniorId, java.time.LocalDateTime before);
    Optional<SensorEvent> findTopBySeniorIdOrderByEventTimeDesc(Long seniorId);
}
