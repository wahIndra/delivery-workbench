package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.DashboardSlaMetricsResponse;
import com.deliveryworkbench.dto.RequestAgingResponse;
import com.deliveryworkbench.service.SlaAndAgingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "SLA and Aging", description = "Endpoints for SLA tracking and aging metrics")
public class SlaAndAgingController {

    private final SlaAndAgingService slaService;

    @GetMapping("/requests/{requestId}/aging")
    @Operation(summary = "Get SLA and aging status for a specific request")
    public ResponseEntity<RequestAgingResponse> getAgingForRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(slaService.getAgingForRequest(requestId));
    }

    @GetMapping("/dashboard/sla-metrics")
    @Operation(summary = "Get SLA and aging metrics for the dashboard")
    public ResponseEntity<DashboardSlaMetricsResponse> getDashboardMetrics() {
        return ResponseEntity.ok(slaService.getDashboardMetrics());
    }
}
