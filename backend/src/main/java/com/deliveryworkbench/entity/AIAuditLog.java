package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Immutable audit log of every AI invocation in the system.
 *
 * <p>BR-03: Every AI-generated output must be saved here before returning to the caller.
 * SG-05: This table is immutable — no update or delete operations are permitted.
 * The repository must only expose save and findBy methods.
 *
 * <p>This record stores the full input prompt sent to AI and the full output received,
 * so that every AI action is auditable and reviewable by humans.
 */
@Entity
@Table(name = "ai_audit_logs",
    indexes = {
        @Index(name = "idx_aal_request_id", columnList = "request_id"),
        @Index(name = "idx_aal_action_type", columnList = "ai_action_type"),
        @Index(name = "idx_aal_created_at", columnList = "created_at")
    })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ai_audit_logs_seq")
    @SequenceGenerator(name = "ai_audit_logs_seq", sequenceName = "ai_audit_logs_seq", allocationSize = 1)
    private Long id;

    /** May be null if the AI action is not tied to a specific delivery request. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private DeliveryRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_action_type", nullable = false, length = 50)
    private AIActionType aiActionType;

    @Column(name = "input_prompt", columnDefinition = "TEXT")
    private String inputPrompt;

    @Column(name = "output_text", columnDefinition = "TEXT")
    private String outputText;

    /** Username of the user who triggered the AI action. */
    @Column(name = "requested_by", nullable = false, length = 100)
    private String requestedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
