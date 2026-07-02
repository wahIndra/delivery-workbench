package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.DecisionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDecisionLogRequest {

    @NotBlank(message = "Decision title is required")
    private String decisionTitle;

    @NotBlank(message = "Decision description is required")
    private String decisionDescription;

    @NotNull(message = "Decision type is required")
    private DecisionType decisionType;

    @NotBlank(message = "Decided by is required")
    private String decidedBy;

    @NotNull(message = "Decision date is required")
    private LocalDate decisionDate;

    private String impact;
}
