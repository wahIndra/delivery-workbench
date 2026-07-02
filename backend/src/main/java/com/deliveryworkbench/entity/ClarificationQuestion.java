package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Clarification question tied to a delivery request.
 * Source can be HUMAN or AI. AI-generated questions are drafts that must be
 * reviewed and approved by a human before being sent to business users (BR-04).
 */
@Entity
@Table(name = "clarification_questions",
    indexes = {
        @Index(name = "idx_cq_request_id", columnList = "request_id"),
        @Index(name = "idx_cq_status", columnList = "status")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClarificationQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clarification_questions_seq")
    @SequenceGenerator(name = "clarification_questions_seq", sequenceName = "clarification_questions_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    /** Username who asked the question (or "AI" for MockAIService-generated). */
    @Column(name = "asked_by", length = 100)
    private String askedBy;

    /** Username who answered the question. Null until answered. */
    @Column(name = "answered_by", length = 100)
    private String answeredBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private QuestionSource source = QuestionSource.HUMAN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private QuestionStatus status = QuestionStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "answered_at")
    private OffsetDateTime answeredAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
