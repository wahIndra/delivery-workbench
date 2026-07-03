package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.RiskCategory;
import com.deliveryworkbench.entity.RiskImpact;
import com.deliveryworkbench.entity.RiskProbability;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRiskRegisterRequest {
    @NotBlank(message = "Risk title is required")
    private String riskTitle;
    private String riskDescription;
    
    @NotNull(message = "Risk category is required")
    private RiskCategory riskCategory;
    
    @NotNull(message = "Probability is required")
    private RiskProbability probability;
    
    @NotNull(message = "Impact is required")
    private RiskImpact impact;
    
    private String mitigationPlan;
    private String owner;
}
