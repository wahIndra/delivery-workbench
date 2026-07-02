package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.DeliveryStageHistory;
import com.deliveryworkbench.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for DeliveryStageHistory — insert-only (BR-06, SG-06).
 * No delete or update methods are exposed.
 */
@Repository
public interface DeliveryStageHistoryRepository extends JpaRepository<DeliveryStageHistory, Long> {

    List<DeliveryStageHistory> findByRequest_IdOrderByChangedAtAsc(Long requestId);

    /** Count how many times a request re-entered NEED_CLARIFICATION (rework indicator). */
    @Query("""
        SELECT COUNT(h) FROM DeliveryStageHistory h
        WHERE h.request.id = :requestId
        AND h.toStatus = :status
        """)
    long countTransitionsToStatus(
            @Param("requestId") Long requestId,
            @Param("status") RequestStatus status);

    /** All history across all requests for dashboard analysis. */
    @Query("""
        SELECT h FROM DeliveryStageHistory h
        WHERE h.request.id IN :requestIds
        ORDER BY h.changedAt ASC
        """)
    List<DeliveryStageHistory> findByRequestIds(@Param("requestIds") List<Long> requestIds);
}
