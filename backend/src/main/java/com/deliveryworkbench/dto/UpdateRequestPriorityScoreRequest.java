package com.deliveryworkbench.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateRequestPriorityScoreRequest {
    @Min(1) @Max(5) private Integer businessImpactScore;
    @Min(1) @Max(5) private Integer urgencyScore;
    @Min(1) @Max(5) private Integer regulatoryImpactScore;
    @Min(1) @Max(5) private Integer customerImpactScore;
    @Min(1) @Max(5) private Integer operationalRiskScore;
    @Min(1) @Max(5) private Integer technicalComplexityScore;
    @Min(1) @Max(5) private Integer dependencyScore;
    private String scoringNotes;
}
