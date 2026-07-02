package com.deliveryworkbench.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NextBestActionRecommendation {
    private String recommendation;
    private String reason;
}
