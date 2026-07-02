package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Priority score for a delivery request.
 * Contains sub-scores (1-5) used to calculate a total score and recommendation.
 */
@Entity
@Table(name = "request_priority_scores",
    indexes = {
        @Index(name = "idx_pri_score_request_id", columnList = "request_id", unique = true)
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPriorityScore {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pri_scores_seq")
    @SequenceGenerator(name = "pri_scores_seq", sequenceName = "pri_scores_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private DeliveryRequest request;

    @Builder.Default private Integer businessImpactScore = 1;
    @Builder.Default private Integer urgencyScore = 1;
    @Builder.Default private Integer regulatoryImpactScore = 1;
    @Builder.Default private Integer customerImpactScore = 1;
    @Builder.Default private Integer operationalRiskScore = 1;
    @Builder.Default private Integer technicalComplexityScore = 1;
    @Builder.Default private Integer dependencyScore = 1;

    @Column(name = "total_score")
    @Builder.Default
    private Integer totalScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_recommendation", length = 20)
    private PriorityRecommendation priorityRecommendation;

    @Column(name = "scoring_notes", columnDefinition = "TEXT")
    private String scoringNotes;

    @Column(name = "scored_by", length = 100)
    private String scoredBy;

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
