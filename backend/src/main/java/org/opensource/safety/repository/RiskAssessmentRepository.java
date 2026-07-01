package org.opensource.safety.repository;

import java.util.Optional;
import org.opensource.safety.domain.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    Optional<RiskAssessment> findTopBySeniorIdOrderByAssessedAtDesc(Long seniorId);
}
