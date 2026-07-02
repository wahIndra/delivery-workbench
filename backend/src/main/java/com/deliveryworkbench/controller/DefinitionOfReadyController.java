package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.DefinitionOfReadyResponse;
import com.deliveryworkbench.dto.UpdateDefinitionOfReadyRequest;
import com.deliveryworkbench.service.DefinitionOfReadyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests/{requestId}/dor")
@RequiredArgsConstructor
public class DefinitionOfReadyController {

    private final DefinitionOfReadyService dorService;

    @GetMapping
    public ResponseEntity<DefinitionOfReadyResponse> getChecklist(@PathVariable Long requestId) {
        return ResponseEntity.ok(dorService.getByRequestId(requestId));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'ADMIN')")
    public ResponseEntity<DefinitionOfReadyResponse> updateChecklist(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateDefinitionOfReadyRequest request) {
        return ResponseEntity.ok(dorService.updateChecklist(requestId, request));
    }
}
