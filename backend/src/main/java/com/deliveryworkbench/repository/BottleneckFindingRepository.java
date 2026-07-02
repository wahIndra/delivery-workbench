package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.BottleneckFinding;
import com.deliveryworkbench.entity.FindingStatus;
import com.deliveryworkbench.entity.FindingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BottleneckFindingRepository extends JpaRepository<BottleneckFinding, Long> {
    List<BottleneckFinding> findByRequest_IdOrderByCreatedAtDesc(Long requestId);
    List<BottleneckFinding> findByStatus(FindingStatus status);
    boolean existsByRequest_IdAndFindingTypeAndStatus(Long requestId, FindingType findingType, FindingStatus status);
}
