package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/** Request body for creating a new delivery request. */
@Data
public class CreateDeliveryRequestRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @NotBlank(message = "Business problem is required")
    private String businessProblem;

    private String expectedOutcome;
    private String currentProcess;
    private String proposedChange;
    private String impactedUsers;
    private String impactedChannels;
    private String impactedSystems;

    @NotNull(message = "Priority is required")
    private Priority priority;

    private LocalDate deadline;
    private String deadlineReason;
    private String businessOwner;
    private String itOwner;
    private String uatPic;
}
