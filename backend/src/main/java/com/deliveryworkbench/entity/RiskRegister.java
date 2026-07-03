package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "risk_registers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "risk_registers_seq")
    @SequenceGenerator(name = "risk_registers_seq", sequenceName = "risk_registers_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Column(name = "risk_title", nullable = false)
    private String riskTitle;

    @Column(name = "risk_description", columnDefinition = "TEXT")
    private String riskDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", nullable = false)
    private RiskCategory riskCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "probability", nullable = false)
    private RiskProbability probability;

    @Enumerated(EnumType.STRING)
    @Column(name = "impact", nullable = false)
    private RiskImpact impact;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "mitigation_plan", columnDefinition = "TEXT")
    private String mitigationPlan;

    @Column(name = "owner", length = 100)
    private String owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RiskStatus status;

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
