package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents a suggested next best action for a delivery request.
 */
@Entity
@Table(name = "next_best_actions",
    indexes = {
        @Index(name = "idx_nba_request_id", columnList = "request_id"),
        @Index(name = "idx_nba_status", columnList = "status")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NextBestAction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "next_best_actions_seq")
    @SequenceGenerator(name = "next_best_actions_seq", sequenceName = "next_best_actions_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String recommendation;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "suggested_owner", length = 100)
    private String suggestedOwner;

    @Column(name = "suggested_due_date")
    private OffsetDateTime suggestedDueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ActionStatus status = ActionStatus.PROPOSED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "accepted_at")
    private OffsetDateTime acceptedAt;

    @Column(name = "rejected_at")
    private OffsetDateTime rejectedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
