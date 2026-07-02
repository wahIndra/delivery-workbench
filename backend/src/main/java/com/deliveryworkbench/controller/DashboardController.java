package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.DashboardMetricsResponse;
import com.deliveryworkbench.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('MANAGEMENT_VIEWER', 'ADMIN', 'SYSTEM_ANALYST', 'SOLUTION_ARCHITECT', 'RELEASE_MANAGER', 'QA', 'DEVELOPER')")
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetrics() {
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }
}
