package com.deliveryworkbench.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RequirementVersionDto {
    private Long id;
    private Long requirementId;
    private Long requestId;
    private Integer version;
    private String scope;
    private String outOfScope;
    private String userStory;
    private String acceptanceCriteria;
    private String assumptions;
    private String dependencies;
    private String changeReason;
    private String changedBy;
    private OffsetDateTime createdAt;
}
