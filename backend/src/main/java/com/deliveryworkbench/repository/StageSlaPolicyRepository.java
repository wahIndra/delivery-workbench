package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.entity.StageSlaPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StageSlaPolicyRepository extends JpaRepository<StageSlaPolicy, Long> {
    Optional<StageSlaPolicy> findByStageAndActiveTrue(RequestStatus stage);
}
