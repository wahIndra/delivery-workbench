package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.Priority;
import com.deliveryworkbench.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Response DTO for a delivery request — used in lists and detail views. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestResponse {
    private Long id;
    private String requestCode;
    private String title;
    private String businessProblem;
    private String expectedOutcome;
    private String currentProcess;
    private String proposedChange;
    private String impactedUsers;
    private String impactedChannels;
    private String impactedSystems;
    private Priority priority;
    private LocalDate deadline;
    private String deadlineReason;
    private String businessOwner;
    private String itOwner;
    private String uatPic;
    private AppUserResponse requester;
    private RequestStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
