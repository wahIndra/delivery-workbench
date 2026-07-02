package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.BottleneckFindingResponse;
import com.deliveryworkbench.dto.UpdateFindingStatusRequest;
import com.deliveryworkbench.service.BottleneckAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Bottleneck Detection", description = "Endpoints for detecting and managing delivery bottlenecks")
public class BottleneckController {

    private final BottleneckAnalysisService bottleneckService;

    @PostMapping("/requests/{requestId}/bottlenecks/analyze")
    @Operation(summary = "Run bottleneck detection analysis for a specific request")
    public ResponseEntity<List<BottleneckFindingResponse>> analyzeRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(bottleneckService.analyzeRequest(requestId));
    }

    @GetMapping("/requests/{requestId}/bottlenecks")
    @Operation(summary = "Get all bottleneck findings for a specific request")
    public ResponseEntity<List<BottleneckFindingResponse>> getFindingsForRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(bottleneckService.getFindingsForRequest(requestId));
    }

    @GetMapping("/dashboard/bottlenecks/active")
    @Operation(summary = "Get all active bottleneck findings across all requests")
    public ResponseEntity<List<BottleneckFindingResponse>> getActiveFindings() {
        return ResponseEntity.ok(bottleneckService.getActiveFindings());
    }

    @PutMapping("/requests/{requestId}/bottlenecks/{findingId}/status")
    @Operation(summary = "Acknowledge, resolve, or ignore a bottleneck finding")
    public ResponseEntity<BottleneckFindingResponse> updateFindingStatus(
            @PathVariable Long requestId,
            @PathVariable Long findingId,
            @Valid @RequestBody UpdateFindingStatusRequest request) {
        return ResponseEntity.ok(bottleneckService.updateFindingStatus(findingId, request));
    }
}
