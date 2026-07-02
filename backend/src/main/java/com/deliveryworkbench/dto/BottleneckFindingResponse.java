package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.DetectedBy;
import com.deliveryworkbench.entity.FindingSeverity;
import com.deliveryworkbench.entity.FindingStatus;
import com.deliveryworkbench.entity.FindingType;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class BottleneckFindingResponse {
    private Long id;
    private Long requestId;
    private FindingType findingType;
    private FindingSeverity severity;
    private String description;
    private String recommendedAction;
    private DetectedBy detectedBy;
    private FindingStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;
}
