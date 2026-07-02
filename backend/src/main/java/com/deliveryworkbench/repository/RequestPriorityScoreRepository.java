package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.RequestPriorityScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestPriorityScoreRepository extends JpaRepository<RequestPriorityScore, Long> {
    Optional<RequestPriorityScore> findByRequest_Id(Long requestId);
    boolean existsByRequest_Id(Long requestId);
}
