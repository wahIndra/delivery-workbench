package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.RequestPriorityScoreResponse;
import com.deliveryworkbench.dto.UpdateRequestPriorityScoreRequest;
import com.deliveryworkbench.service.RequestPriorityScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests/{requestId}/priority-score")
@RequiredArgsConstructor
@Tag(name = "Priority Scoring", description = "Endpoints for request priority scoring and recommendations")
public class RequestPriorityScoreController {

    private final RequestPriorityScoreService scoreService;

    @GetMapping
    @Operation(summary = "Get priority score for a request")
    public ResponseEntity<RequestPriorityScoreResponse> getScore(@PathVariable Long requestId) {
        return ResponseEntity.ok(scoreService.getScore(requestId));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'BUSINESS_OWNER', 'RELEASE_MANAGER', 'ADMIN')")
    @Operation(summary = "Update priority score criteria")
    public ResponseEntity<RequestPriorityScoreResponse> updateScore(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateRequestPriorityScoreRequest request) {
        return ResponseEntity.ok(scoreService.updateScore(requestId, request));
    }

    @PostMapping("/generate-ai")
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'BUSINESS_OWNER', 'RELEASE_MANAGER', 'ADMIN')")
    @Operation(summary = "Generate AI priority recommendation")
    public ResponseEntity<RequestPriorityScoreResponse> generateAiRecommendation(@PathVariable Long requestId) {
        return ResponseEntity.ok(scoreService.generateAiRecommendation(requestId));
    }
}
