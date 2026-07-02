package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Snapshot of a request's aging against its SLA at a specific point in time.
 * This can be used for historical reporting and trend analysis.
 */
@Entity
@Table(name = "request_aging_snapshots",
    indexes = {
        @Index(name = "idx_aging_request_id", columnList = "request_id"),
        @Index(name = "idx_aging_calculated_at", columnList = "calculated_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestAgingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aging_snapshots_seq")
    @SequenceGenerator(name = "aging_snapshots_seq", sequenceName = "aging_snapshots_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 30)
    private RequestStatus currentStatus;

    @Column(name = "entered_status_at", nullable = false)
    private OffsetDateTime enteredStatusAt;

    @Column(name = "aging_hours", nullable = false)
    private Integer agingHours;

    @Column(name = "sla_hours")
    private Integer slaHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "sla_status", nullable = false, length = 20)
    private SlaStatus slaStatus;

    @Column(name = "calculated_at", nullable = false)
    private OffsetDateTime calculatedAt;
}
