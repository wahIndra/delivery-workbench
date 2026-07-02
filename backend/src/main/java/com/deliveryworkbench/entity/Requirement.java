package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Versioned requirement for a delivery request.
 * Contains user story and acceptance criteria drafted by System Analyst.
 * AI may generate drafts; humans must approve (BR-05).
 */
@Entity
@Table(name = "requirements",
    indexes = {
        @Index(name = "idx_req_request_id", columnList = "request_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requirements_seq")
    @SequenceGenerator(name = "requirements_seq", sequenceName = "requirements_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Column(columnDefinition = "TEXT")
    private String scope;

    @Column(name = "out_of_scope", columnDefinition = "TEXT")
    private String outOfScope;

    @Column(name = "user_story", columnDefinition = "TEXT")
    private String userStory;

    @Column(name = "acceptance_criteria", columnDefinition = "TEXT")
    private String acceptanceCriteria;

    @Column(columnDefinition = "TEXT")
    private String assumptions;

    @Column(columnDefinition = "TEXT")
    private String dependencies;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "DRAFT";

    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;

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
