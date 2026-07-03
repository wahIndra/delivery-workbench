package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ReleaseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ReleaseScheduleDto {
    private Long id;
    private Long requestId;
    private String releaseTitle;
    private OffsetDateTime plannedReleaseDate;
    private OffsetDateTime actualReleaseDate;
    private String releaseWindow;
    private String releaseManager;
    private ReleaseStatus releaseStatus;
    private String rollbackPlan;
    private String releaseNotes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
