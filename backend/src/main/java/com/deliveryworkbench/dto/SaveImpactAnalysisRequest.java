package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.AnalysisStatus;
import com.deliveryworkbench.entity.RiskLevel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveImpactAnalysisRequest {

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

    @NotNull(message = "Risk level is required")
    private RiskLevel riskLevel;

    private String mitigationPlan;

    @NotNull(message = "Status is required")
    private AnalysisStatus status;
}
