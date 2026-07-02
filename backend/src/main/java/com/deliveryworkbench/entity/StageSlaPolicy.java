package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Defines the SLA rules for each delivery stage.
 */
@Entity
@Table(name = "stage_sla_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageSlaPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stage_sla_policies_seq")
    @SequenceGenerator(name = "stage_sla_policies_seq", sequenceName = "stage_sla_policies_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, unique = true)
    private RequestStatus stage;

    @Column(name = "sla_hours", nullable = false)
    private Integer slaHours;

    @Column(name = "warning_threshold_hours", nullable = false)
    private Integer warningThresholdHours;

    @Column(name = "escalation_threshold_hours", nullable = false)
    private Integer escalationThresholdHours;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
