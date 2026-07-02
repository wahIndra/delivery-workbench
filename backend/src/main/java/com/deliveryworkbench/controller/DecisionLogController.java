package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.CreateDecisionLogRequest;
import com.deliveryworkbench.dto.DecisionLogDto;
import com.deliveryworkbench.service.DecisionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/decision-logs")
@RequiredArgsConstructor
@Tag(name = "Decision Log", description = "Endpoints for Decision Log management")
public class DecisionLogController {

    private final DecisionLogService decisionLogService;

    @PostMapping
    @Operation(summary = "Create a new decision log")
    public ResponseEntity<DecisionLogDto> createDecisionLog(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateDecisionLogRequest request) {
        return ResponseEntity.ok(decisionLogService.createDecisionLog(requestId, request));
    }

    @GetMapping
    @Operation(summary = "Get decision logs for a request")
    public ResponseEntity<List<DecisionLogDto>> getDecisionLogs(@PathVariable Long requestId) {
        return ResponseEntity.ok(decisionLogService.getDecisionLogsByRequestId(requestId));
    }
}
