package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.PriorityRecommendation;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RequestPriorityScoreResponse {
    private Long id;
    private Long requestId;
    private Integer businessImpactScore;
    private Integer urgencyScore;
    private Integer regulatoryImpactScore;
    private Integer customerImpactScore;
    private Integer operationalRiskScore;
    private Integer technicalComplexityScore;
    private Integer dependencyScore;
    private Integer totalScore;
    private PriorityRecommendation priorityRecommendation;
    private String scoringNotes;
    private String scoredBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
