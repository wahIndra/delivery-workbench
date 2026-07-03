package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.RiskCategory;
import com.deliveryworkbench.entity.RiskImpact;
import com.deliveryworkbench.entity.RiskProbability;
import com.deliveryworkbench.entity.RiskStatus;
import lombok.Data;

@Data
public class UpdateRiskRegisterRequest {
    private String riskTitle;
    private String riskDescription;
    private RiskCategory riskCategory;
    private RiskProbability probability;
    private RiskImpact impact;
    private String mitigationPlan;
    private String owner;
    private RiskStatus status;
}
