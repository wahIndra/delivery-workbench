package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.AIActionType;
import com.deliveryworkbench.entity.AIAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AIAuditLog repository — read and save only (SG-05).
 * No delete method must ever be added here.
 */
@Repository
public interface AIAuditLogRepository extends JpaRepository<AIAuditLog, Long> {
    List<AIAuditLog> findByRequest_IdOrderByCreatedAtDesc(Long requestId);
    Page<AIAuditLog> findByAiActionType(AIActionType actionType, Pageable pageable);
    Page<AIAuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
