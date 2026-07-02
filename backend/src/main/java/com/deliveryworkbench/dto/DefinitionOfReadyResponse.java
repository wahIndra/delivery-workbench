package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ReadyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefinitionOfReadyResponse {
    private Long id;
    private Long requestId;
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
    private ReadyStatus readyStatus;
    private String reviewedBy;
    private OffsetDateTime reviewedAt;
    private OffsetDateTime updatedAt;
}
