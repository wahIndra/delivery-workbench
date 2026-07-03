package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ReleaseStatus;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class UpdateReleaseScheduleRequest {
    private String releaseTitle;
    private OffsetDateTime plannedReleaseDate;
    private OffsetDateTime actualReleaseDate;
    private String releaseWindow;
    private String releaseManager;
    private ReleaseStatus releaseStatus;
    private String rollbackPlan;
    private String releaseNotes;
}
