package com.deliveryworkbench.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardSlaMetricsResponse {
    private List<RequestAgingResponse> breachedRequests;
    private List<RequestAgingResponse> warningRequests;
    private Map<String, Double> averageAgingByStage;
    private List<RequestAgingResponse> oldestRequestsByStage;
}
