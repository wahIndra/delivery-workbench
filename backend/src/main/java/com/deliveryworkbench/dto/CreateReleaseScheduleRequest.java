package com.deliveryworkbench.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class CreateReleaseScheduleRequest {
    @NotBlank(message = "Release title is required")
    private String releaseTitle;
    private OffsetDateTime plannedReleaseDate;
    private String releaseWindow;
    private String releaseManager;
}
