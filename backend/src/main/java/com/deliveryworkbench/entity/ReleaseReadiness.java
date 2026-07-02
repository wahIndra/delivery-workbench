package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Release readiness checklist — 12-point gate before READY_FOR_RELEASE.
 *
 * <p>BR-02: A request can only move to READY_FOR_RELEASE when readyForRelease == true.
 * BR-10: uatSignedOff must also be true before READY_FOR_RELEASE.
 * Both rules are enforced in the service layer, not here.
 * AI must never set readyForRelease or approve any checklist item (BR-05).
 */
@Entity
@Table(name = "release_readiness",
    indexes = {
        @Index(name = "idx_rr_request_id", columnList = "request_id", unique = true)
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseReadiness {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "release_readiness_seq")
    @SequenceGenerator(name = "release_readiness_seq", sequenceName = "release_readiness_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private DeliveryRequest request;

    // ── 12-point checklist ─────────────────────────────────────────────────────

    @Builder.Default private boolean requirementSignedOff = false;
    @Builder.Default private boolean solutionDesignApproved = false;
    @Builder.Default private boolean codeReviewed = false;
    @Builder.Default private boolean sitPassed = false;

    /** BR-10: UAT signoff required before READY_FOR_RELEASE. Human approval only (BR-05). */
    @Builder.Default private boolean uatSignedOff = false;

    @Builder.Default private boolean securityReviewed = false;
    @Builder.Default private boolean dbScriptReviewed = false;
    @Builder.Default private boolean rollbackPlanAvailable = false;
    @Builder.Default private boolean monitoringPrepared = false;
    @Builder.Default private boolean releaseNotePrepared = false;
    @Builder.Default private boolean supportPicAssigned = false;

    /**
     * Master release gate flag. Only true when all checklist items are complete.
     * Must be set by Release Manager — AI may not approve this (BR-05).
     */
    @Builder.Default
    private boolean readyForRelease = false;

    /** Username of the Release Manager who reviewed and approved. */
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
