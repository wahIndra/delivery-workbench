package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "decision_logs",
        indexes = {
                @Index(name = "idx_dl_request_id", columnList = "request_id"),
                @Index(name = "idx_dl_created_at", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "decision_logs_seq")
    @SequenceGenerator(name = "decision_logs_seq", sequenceName = "decision_logs_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Column(name = "decision_title", nullable = false, length = 255)
    private String decisionTitle;

    @Column(name = "decision_description", columnDefinition = "TEXT", nullable = false)
    private String decisionDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_type", nullable = false, length = 30)
    private DecisionType decisionType;

    @Column(name = "decided_by", nullable = false, length = 100)
    private String decidedBy;

    @Column(name = "decision_date", nullable = false)
    private LocalDate decisionDate;

    @Column(name = "impact", columnDefinition = "TEXT")
    private String impact;

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
