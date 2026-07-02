package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.ActionStatus;
import com.deliveryworkbench.entity.NextBestAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NextBestActionRepository extends JpaRepository<NextBestAction, Long> {
    List<NextBestAction> findByRequest_IdOrderByCreatedAtDesc(Long requestId);
    List<NextBestAction> findByRequest_IdAndStatus(Long requestId, ActionStatus status);
}
