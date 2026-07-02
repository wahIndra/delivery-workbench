package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {

    Optional<DeliveryRequest> findByRequestCode(String requestCode);

    boolean existsByRequestCode(String requestCode);

    Page<DeliveryRequest> findByStatus(RequestStatus status, Pageable pageable);

    Page<DeliveryRequest> findByRequester_Username(String username, Pageable pageable);

    @Query("""
        SELECT dr FROM DeliveryRequest dr
        WHERE (:status IS NULL OR dr.status = :status)
        AND (:keyword IS NULL OR LOWER(dr.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(dr.requestCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<DeliveryRequest> search(
            @Param("status") RequestStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);

    List<DeliveryRequest> findByStatus(RequestStatus status);

    /** Aging requests: created before a threshold and not yet released or cancelled. */
    @Query("""
        SELECT dr FROM DeliveryRequest dr
        WHERE dr.createdAt < :threshold
        AND dr.status NOT IN (
            com.deliveryworkbench.entity.RequestStatus.RELEASED,
            com.deliveryworkbench.entity.RequestStatus.CANCELLED)
        ORDER BY dr.createdAt ASC
        """)
    List<DeliveryRequest> findAgingRequests(@Param("threshold") OffsetDateTime threshold);

    /** Count by each status — used for dashboard summary. */
    @Query("SELECT dr.status, COUNT(dr) FROM DeliveryRequest dr GROUP BY dr.status")
    List<Object[]> countGroupedByStatus();
}
