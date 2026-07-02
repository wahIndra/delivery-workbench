package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Central domain entity — represents an IT delivery request from intake to release.
 *
 * <p>Business rules enforced in service layer (not here):
 * <ul>
 *   <li>BR-01: Cannot move to READY_FOR_DEVELOPMENT without DoR == READY.
 *   <li>BR-02: Cannot move to READY_FOR_RELEASE without ReleaseReadiness.readyForRelease.
 *   <li>BR-06: Every status change must be recorded in DeliveryStageHistory.
 *   <li>BR-09: businessOwner and itOwner required before READY_FOR_ANALYSIS.
 * </ul>
 */
@Entity
@Table(name = "delivery_requests",
    indexes = {
        @Index(name = "idx_dr_status", columnList = "status"),
        @Index(name = "idx_dr_request_code", columnList = "request_code", unique = true),
        @Index(name = "idx_dr_requester_id", columnList = "requester_id"),
        @Index(name = "idx_dr_created_at", columnList = "created_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_requests_seq")
    @SequenceGenerator(name = "delivery_requests_seq", sequenceName = "delivery_requests_seq", allocationSize = 1)
    private Long id;

    @Column(name = "request_code", nullable = false, length = 30, unique = true)
    private String requestCode;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "business_problem", columnDefinition = "TEXT")
    private String businessProblem;

    @Column(name = "expected_outcome", columnDefinition = "TEXT")
    private String expectedOutcome;

    @Column(name = "current_process", columnDefinition = "TEXT")
    private String currentProcess;

    @Column(name = "proposed_change", columnDefinition = "TEXT")
    private String proposedChange;

    @Column(name = "impacted_users", columnDefinition = "TEXT")
    private String impactedUsers;

    @Column(name = "impacted_channels", columnDefinition = "TEXT")
    private String impactedChannels;

    @Column(name = "impacted_systems", columnDefinition = "TEXT")
    private String impactedSystems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "deadline_reason", columnDefinition = "TEXT")
    private String deadlineReason;

    // ── Stakeholders (stored by username) ────────────────────────────────────

    /** Username of the Business Owner (BR-09: required before READY_FOR_ANALYSIS). */
    @Column(name = "business_owner", length = 100)
    private String businessOwner;

    /** Username of the IT owner / Solution Architect (BR-09: required before READY_FOR_ANALYSIS). */
    @Column(name = "it_owner", length = 100)
    private String itOwner;

    /** Username who submitted the request. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private AppUser requester;

    /** Username of the UAT PIC (required for UAT signoff). */
    @Column(name = "uat_pic", length = 100)
    private String uatPic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RequestStatus status = RequestStatus.DRAFT;

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
