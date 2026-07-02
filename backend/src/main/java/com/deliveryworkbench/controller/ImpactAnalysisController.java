package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.ImpactAnalysisResponse;
import com.deliveryworkbench.dto.SaveImpactAnalysisRequest;
import com.deliveryworkbench.service.ImpactAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests/{requestId}/impact-analysis")
@RequiredArgsConstructor
public class ImpactAnalysisController {

    private final ImpactAnalysisService impactAnalysisService;

    @GetMapping
    public ResponseEntity<ImpactAnalysisResponse> getImpactAnalysis(@PathVariable Long requestId) {
        return ResponseEntity.ok(impactAnalysisService.getByRequestId(requestId));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SOLUTION_ARCHITECT', 'ADMIN')")
    public ResponseEntity<ImpactAnalysisResponse> saveImpactAnalysis(
            @PathVariable Long requestId,
            @Valid @RequestBody SaveImpactAnalysisRequest request) {
        return ResponseEntity.ok(impactAnalysisService.saveImpactAnalysis(requestId, request));
    }

    @PostMapping("/ai-generate")
    @PreAuthorize("hasAnyRole('SOLUTION_ARCHITECT', 'ADMIN')")
    public ResponseEntity<ImpactAnalysisResponse> generateAiAnalysis(@PathVariable Long requestId) {
        return ResponseEntity.ok(impactAnalysisService.generateAiAnalysis(requestId));
    }
}
