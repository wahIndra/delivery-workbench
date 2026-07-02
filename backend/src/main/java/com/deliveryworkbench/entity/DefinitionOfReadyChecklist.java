package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Definition of Ready checklist — 16-point gate before READY_FOR_DEVELOPMENT.
 *
 * <p>BR-01: A request can only move to READY_FOR_DEVELOPMENT when readyStatus == READY.
 * This is enforced in the service layer, not here.
 * AI must never set readyStatus — only a human reviewer can (BR-05).
 */
@Entity
@Table(name = "definition_of_ready_checklists",
    indexes = {
        @Index(name = "idx_dor_request_id", columnList = "request_id", unique = true)
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefinitionOfReadyChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dor_checklists_seq")
    @SequenceGenerator(name = "dor_checklists_seq", sequenceName = "dor_checklists_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private DeliveryRequest request;

    // ── 16-point checklist ────────────────────────────────────────────────────

    @Builder.Default private boolean businessProblemClear = false;
    @Builder.Default private boolean expectedOutcomeDefined = false;
    @Builder.Default private boolean scopeAgreed = false;
    @Builder.Default private boolean outOfScopeAgreed = false;
    @Builder.Default private boolean impactedUsersIdentified = false;
    @Builder.Default private boolean impactedSystemsIdentified = false;
    @Builder.Default private boolean processFlowDocumented = false;
    @Builder.Default private boolean dataRequirementListed = false;
    @Builder.Default private boolean integrationRequirementListed = false;
    @Builder.Default private boolean acceptanceCriteriaAgreed = false;
    @Builder.Default private boolean priorityClear = false;
    @Builder.Default private boolean deadlineReasonClear = false;
    @Builder.Default private boolean risksIdentified = false;
    @Builder.Default private boolean businessOwnerAssigned = false;
    @Builder.Default private boolean itOwnerAssigned = false;
    @Builder.Default private boolean testerAssigned = false;

    /**
     * Computed readiness gate. Only READY allows transition to READY_FOR_DEVELOPMENT (BR-01).
     * AI must never set this field (BR-05).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ready_status", nullable = false, length = 20)
    @Builder.Default
    private ReadyStatus readyStatus = ReadyStatus.NOT_READY;

    /** Username of the reviewer who assessed readiness. Null until reviewed. */
    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

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
