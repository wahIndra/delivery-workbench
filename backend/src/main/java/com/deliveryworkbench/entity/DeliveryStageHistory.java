package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Immutable audit record of every delivery request status transition.
 * BR-06: All status changes must be recorded here — no delete or update allowed.
 * This entity is insert-only. The repository must not expose update or delete operations.
 */
@Entity
@Table(name = "delivery_stage_history",
    indexes = {
        @Index(name = "idx_dsh_request_id", columnList = "request_id"),
        @Index(name = "idx_dsh_changed_at", columnList = "changed_at")
    })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryStageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_stage_history_seq")
    @SequenceGenerator(name = "delivery_stage_history_seq", sequenceName = "delivery_stage_history_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private RequestStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private RequestStatus toStatus;

    /** Username of the person who triggered the transition. */
    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private OffsetDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = OffsetDateTime.now();
    }
}
