package org.opensource.safety.repository;

import java.util.List;
import org.opensource.safety.domain.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    List<AlertHistory> findTop20BySeniorIdOrderByCreatedAtDesc(Long seniorId);
}
