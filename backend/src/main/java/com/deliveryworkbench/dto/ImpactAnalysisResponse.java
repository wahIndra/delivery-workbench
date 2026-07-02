package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.AnalysisStatus;
import com.deliveryworkbench.entity.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImpactAnalysisResponse {
    private Long id;
    private Long requestId;
    private String impactedApplications;
    private String impactedDatabases;
    private String impactedApis;
    private String impactedJobs;
    private String impactedQueues;
    private String integrationImpact;
    private String securityImpact;
    private String performanceImpact;
    private String operationalImpact;
    private String dataImpact;
    private RiskLevel riskLevel;
    private String mitigationPlan;
    private String reviewedBy;
    private AnalysisStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
