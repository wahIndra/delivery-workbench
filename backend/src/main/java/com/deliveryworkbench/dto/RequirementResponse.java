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
public class RequirementResponse {
    private Long id;
    private Long requestId;
    private String scope;
    private String outOfScope;
    private String userStory;
    private String acceptanceCriteria;
    private String assumptions;
    private String dependencies;
    private String status;
    private Integer version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
