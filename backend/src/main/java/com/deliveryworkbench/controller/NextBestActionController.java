package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.NextBestActionResponse;
import com.deliveryworkbench.dto.UpdateNextBestActionRequest;
import com.deliveryworkbench.service.NextBestActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/next-best-actions")
@RequiredArgsConstructor
@Tag(name = "Next Best Action", description = "Endpoints for AI Next Best Action recommendation")
public class NextBestActionController {

    private final NextBestActionService nbaService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a new Next Best Action via AI")
    public ResponseEntity<NextBestActionResponse> generateAction(
            @PathVariable Long requestId,
            @RequestHeader(value = "X-User", defaultValue = "system") String user) {
        return ResponseEntity.ok(nbaService.generateNextBestAction(requestId, user));
    }

    @GetMapping
    @Operation(summary = "Get all actions for a request")
    public ResponseEntity<List<NextBestActionResponse>> getActions(@PathVariable Long requestId) {
        return ResponseEntity.ok(nbaService.getActionsForRequest(requestId));
    }

    @PutMapping("/{actionId}/status")
    @Operation(summary = "Update status of an action (e.g., ACCEPTED, DONE)")
    public ResponseEntity<NextBestActionResponse> updateActionStatus(
            @PathVariable Long requestId,
            @PathVariable Long actionId,
            @Valid @RequestBody UpdateNextBestActionRequest request) {
        return ResponseEntity.ok(nbaService.updateActionStatus(actionId, request));
    }
}
