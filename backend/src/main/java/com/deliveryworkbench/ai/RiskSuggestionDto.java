package com.deliveryworkbench.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskSuggestionDto {
    private String riskTitle;
    private String riskDescription;
    private String riskCategory; // Maps to RiskCategory enum
    private String probability; // LOW, MEDIUM, HIGH
    private String impact; // LOW, MEDIUM, HIGH
}
