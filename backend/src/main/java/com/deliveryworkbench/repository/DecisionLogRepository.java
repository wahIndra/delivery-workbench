package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.DecisionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionLogRepository extends JpaRepository<DecisionLog, Long> {
    List<DecisionLog> findByRequest_IdOrderByDecisionDateDesc(Long requestId);
}
