package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.Approval;
import com.deliveryworkbench.entity.ApprovalStatus;
import com.deliveryworkbench.entity.ApprovalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByRequest_IdOrderByCreatedAtDesc(Long requestId);
    Optional<Approval> findTopByRequest_IdAndApprovalTypeOrderByCreatedAtDesc(Long requestId, ApprovalType type);
    boolean existsByRequest_IdAndApprovalTypeAndStatus(Long requestId, ApprovalType type, ApprovalStatus status);
}
