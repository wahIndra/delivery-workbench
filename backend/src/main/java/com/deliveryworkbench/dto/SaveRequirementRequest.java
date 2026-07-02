package com.deliveryworkbench.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveRequirementRequest {

    private String scope;
    private String outOfScope;
    private String userStory;
    private String acceptanceCriteria;
    private String assumptions;
    private String dependencies;

    @NotNull(message = "Status is required")
    private String status;
    private String changeReason;
}
