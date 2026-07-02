package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "requirement_versions",
        indexes = {
                @Index(name = "idx_req_versions_req_id", columnList = "requirement_id"),
                @Index(name = "idx_req_versions_request_id", columnList = "request_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequirementVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requirement_versions_seq")
    @SequenceGenerator(name = "requirement_versions_seq", sequenceName = "requirement_versions_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private Requirement requirement;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(nullable = false)
    private Integer version;

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

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
