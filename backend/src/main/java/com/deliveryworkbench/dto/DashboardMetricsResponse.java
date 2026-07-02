package com.deliveryworkbench.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardMetricsResponse {
    
    // 1. Total requests by status
    private Map<String, Long> totalRequestsByStatus;
    
    // 2. Average Request to Ready time (hours or days, let's use double for days)
    private Double avgRequestToReadyDays;
    
    // 3. Average Ready to Development Start time
    private Double avgReadyToDevStartDays;
    
    // 4. Average Development Cycle Time
    private Double avgDevCycleDays;
    
    // 5. Average SIT duration
    private Double avgSitDays;
    
    // 6. Average UAT duration
    private Double avgUatDays;
    
    // 7. Average UAT Signoff to Release time
    private Double avgUatToReleaseDays;
    
    // 8. Number of requests stuck by stage (e.g. > 14 days in same status)
    private Map<String, Long> stuckRequestsByStage;
    
    // 9. Total Aging requests (total > 30 days overall)
    private Long totalAgingRequests;
    
    // 10. Rework count (total transitions to NEED_CLARIFICATION)
    private Long totalReworkCount;
}
