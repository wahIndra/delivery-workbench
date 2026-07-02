package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.ReleaseReadiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReleaseReadinessRepository extends JpaRepository<ReleaseReadiness, Long> {
    Optional<ReleaseReadiness> findByRequest_Id(Long requestId);
    boolean existsByRequest_Id(Long requestId);
    boolean existsByRequest_IdAndReadyForReleaseTrue(Long requestId);
    boolean existsByRequest_IdAndUatSignedOffTrue(Long requestId);
}
