package com.deliveryworkbench.dto;

import lombok.Data;

@Data
public class UpdateReleaseReadinessRequest {

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
}
