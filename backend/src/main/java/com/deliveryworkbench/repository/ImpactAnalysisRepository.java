package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.ImpactAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImpactAnalysisRepository extends JpaRepository<ImpactAnalysis, Long> {
    Optional<ImpactAnalysis> findByRequest_Id(Long requestId);
    boolean existsByRequest_Id(Long requestId);
}
