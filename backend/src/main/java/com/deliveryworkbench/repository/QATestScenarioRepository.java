package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.QATestScenario;
import com.deliveryworkbench.entity.ScenarioStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QATestScenarioRepository extends JpaRepository<QATestScenario, Long> {
    List<QATestScenario> findByRequest_IdOrderByCreatedAtAsc(Long requestId);
    List<QATestScenario> findByRequest_IdAndStatus(Long requestId, ScenarioStatus status);
    long countByRequest_Id(Long requestId);
}
