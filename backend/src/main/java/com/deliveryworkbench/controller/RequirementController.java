package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.RequirementResponse;
import com.deliveryworkbench.dto.SaveRequirementRequest;
import com.deliveryworkbench.service.RequirementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests/{requestId}/requirement")
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;

    @GetMapping
    public ResponseEntity<RequirementResponse> getRequirement(@PathVariable Long requestId) {
        return ResponseEntity.ok(requirementService.getByRequestId(requestId));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'ADMIN')")
    public ResponseEntity<RequirementResponse> saveRequirement(
            @PathVariable Long requestId,
            @Valid @RequestBody SaveRequirementRequest request) {
        return ResponseEntity.ok(requirementService.saveRequirement(requestId, request));
    }

    @PostMapping("/ai-generate")
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'ADMIN')")
    public ResponseEntity<RequirementResponse> generateAiRequirement(@PathVariable Long requestId) {
        return ResponseEntity.ok(requirementService.generateAiRequirement(requestId));
    }
}
