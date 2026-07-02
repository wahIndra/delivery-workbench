package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents an automatically detected bottleneck or delay in a DeliveryRequest.
 */
@Entity
@Table(name = "bottleneck_findings",
    indexes = {
        @Index(name = "idx_bottleneck_request_id", columnList = "request_id"),
        @Index(name = "idx_bottleneck_status", columnList = "status")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BottleneckFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bottleneck_findings_seq")
    @SequenceGenerator(name = "bottleneck_findings_seq", sequenceName = "bottleneck_findings_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "finding_type", nullable = false, length = 50)
    private FindingType findingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FindingSeverity severity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "recommended_action", columnDefinition = "TEXT")
    private String recommendedAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "detected_by", nullable = false, length = 20)
    private DetectedBy detectedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FindingStatus status = FindingStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
