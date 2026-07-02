package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ReadyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDefinitionOfReadyRequest {

    private boolean businessProblemClear;
    private boolean expectedOutcomeDefined;
    private boolean scopeAgreed;
    private boolean outOfScopeAgreed;
    private boolean impactedUsersIdentified;
    private boolean impactedSystemsIdentified;
    private boolean processFlowDocumented;
    private boolean dataRequirementListed;
    private boolean integrationRequirementListed;
    private boolean acceptanceCriteriaAgreed;
    private boolean priorityClear;
    private boolean deadlineReasonClear;
    private boolean risksIdentified;
    private boolean businessOwnerAssigned;
    private boolean itOwnerAssigned;
    private boolean testerAssigned;

    @NotNull(message = "Ready status is required")
    private ReadyStatus readyStatus;
}
