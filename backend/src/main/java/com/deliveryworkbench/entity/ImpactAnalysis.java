package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Technical impact analysis for a delivery request.
 * Covers applications, databases, APIs, jobs, queues, security, performance,
 * operational, and data impact. Risk level and mitigation plan must be set by
 * a Solution Architect. AI may generate a draft (GENERATE_IMPACT_ANALYSIS_DRAFT)
 * but humans must review and approve (BR-05).
 */
@Entity
@Table(name = "impact_analyses",
    indexes = {
        @Index(name = "idx_ia_request_id", columnList = "request_id", unique = true)
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "impact_analyses_seq")
    @SequenceGenerator(name = "impact_analyses_seq", sequenceName = "impact_analyses_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private DeliveryRequest request;

    @Column(name = "impacted_applications", columnDefinition = "TEXT")
    private String impactedApplications;

    @Column(name = "impacted_databases", columnDefinition = "TEXT")
    private String impactedDatabases;

    @Column(name = "impacted_apis", columnDefinition = "TEXT")
    private String impactedApis;

    @Column(name = "impacted_jobs", columnDefinition = "TEXT")
    private String impactedJobs;

    @Column(name = "impacted_queues", columnDefinition = "TEXT")
    private String impactedQueues;

    @Column(name = "integration_impact", columnDefinition = "TEXT")
    private String integrationImpact;

    @Column(name = "security_impact", columnDefinition = "TEXT")
    private String securityImpact;

    @Column(name = "performance_impact", columnDefinition = "TEXT")
    private String performanceImpact;

    @Column(name = "operational_impact", columnDefinition = "TEXT")
    private String operationalImpact;

    @Column(name = "data_impact", columnDefinition = "TEXT")
    private String dataImpact;

    /** Risk level set by human Solution Architect — AI may only suggest via draft (BR-05). */
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 10)
    @Builder.Default
    private RiskLevel riskLevel = RiskLevel.MEDIUM;

    @Column(name = "mitigation_plan", columnDefinition = "TEXT")
    private String mitigationPlan;

    /** Username of the reviewer. */
    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private AnalysisStatus status = AnalysisStatus.DRAFT;

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
