package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.RiskCategory;
import com.deliveryworkbench.entity.RiskImpact;
import com.deliveryworkbench.entity.RiskProbability;
import com.deliveryworkbench.entity.RiskStatus;
import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Builder
public class RiskRegisterDto {
    private Long id;
    private Long requestId;
    private String riskTitle;
    private String riskDescription;
    private RiskCategory riskCategory;
    private RiskProbability probability;
    private RiskImpact impact;
    private Integer riskScore;
    private String mitigationPlan;
    private String owner;
    private RiskStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
