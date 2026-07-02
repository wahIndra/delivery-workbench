package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.ReleaseReadinessResponse;
import com.deliveryworkbench.dto.UpdateReleaseReadinessRequest;
import com.deliveryworkbench.service.ReleaseReadinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests/{requestId}/release-readiness")
@RequiredArgsConstructor
public class ReleaseReadinessController {

    private final ReleaseReadinessService readinessService;

    @GetMapping
    public ResponseEntity<ReleaseReadinessResponse> getReadiness(@PathVariable Long requestId) {
        return ResponseEntity.ok(readinessService.getReadinessByRequestId(requestId));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('RELEASE_MANAGER', 'ADMIN')")
    public ResponseEntity<ReleaseReadinessResponse> updateReadiness(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateReleaseReadinessRequest request) {
        return ResponseEntity.ok(readinessService.updateReadiness(requestId, request));
    }
}
