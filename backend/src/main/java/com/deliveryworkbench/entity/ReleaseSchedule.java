package com.deliveryworkbench.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "release_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "release_schedules_seq")
    @SequenceGenerator(name = "release_schedules_seq", sequenceName = "release_schedules_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private DeliveryRequest request;

    @Column(name = "release_title", nullable = false)
    private String releaseTitle;

    @Column(name = "planned_release_date")
    private OffsetDateTime plannedReleaseDate;

    @Column(name = "actual_release_date")
    private OffsetDateTime actualReleaseDate;

    @Column(name = "release_window")
    private String releaseWindow;

    @Column(name = "release_manager")
    private String releaseManager;

    @Enumerated(EnumType.STRING)
    @Column(name = "release_status", nullable = false)
    private ReleaseStatus releaseStatus;

    @Column(name = "rollback_plan", columnDefinition = "TEXT")
    private String rollbackPlan;

    @Column(name = "release_notes", columnDefinition = "TEXT")
    private String releaseNotes;

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
