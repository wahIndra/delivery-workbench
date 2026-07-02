package com.deliveryworkbench.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseReadinessResponse {
    private Long id;
    private Long requestId;
    private boolean requirementSignedOff;
    private boolean solutionDesignApproved;
    private boolean codeReviewed;
    private boolean sitPassed;
    private boolean uatSignedOff;
    private boolean securityReviewed;
    private boolean dbScriptReviewed;
    private boolean rollbackPlanAvailable;
    private boolean monitoringPrepared;
    private boolean releaseNotePrepared;
    private boolean supportPicAssigned;
    private boolean readyForRelease;
    private String reviewedBy;
    private OffsetDateTime reviewedAt;
    private OffsetDateTime updatedAt;
}
