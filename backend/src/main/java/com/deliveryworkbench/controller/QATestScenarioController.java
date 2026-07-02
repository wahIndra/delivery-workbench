package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.QATestScenarioResponse;
import com.deliveryworkbench.dto.SaveQATestScenarioRequest;
import com.deliveryworkbench.service.QATestScenarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/scenarios")
@RequiredArgsConstructor
public class QATestScenarioController {

    private final QATestScenarioService scenarioService;

    @GetMapping
    public ResponseEntity<List<QATestScenarioResponse>> getScenarios(@PathVariable Long requestId) {
        return ResponseEntity.ok(scenarioService.getScenariosByRequestId(requestId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('QA', 'ADMIN')")
    public ResponseEntity<QATestScenarioResponse> createScenario(
            @PathVariable Long requestId,
            @Valid @RequestBody SaveQATestScenarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scenarioService.saveScenario(requestId, request, null));
    }

    @PutMapping("/{scenarioId}")
    @PreAuthorize("hasAnyRole('QA', 'ADMIN')")
    public ResponseEntity<QATestScenarioResponse> updateScenario(
            @PathVariable Long requestId,
            @PathVariable Long scenarioId,
            @Valid @RequestBody SaveQATestScenarioRequest request) {
        return ResponseEntity.ok(scenarioService.saveScenario(requestId, request, scenarioId));
    }

    @DeleteMapping("/{scenarioId}")
    @PreAuthorize("hasAnyRole('QA', 'ADMIN')")
    public ResponseEntity<Void> deleteScenario(
            @PathVariable Long requestId,
            @PathVariable Long scenarioId) {
        scenarioService.deleteScenario(scenarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ai-generate")
    @PreAuthorize("hasAnyRole('QA', 'ADMIN')")
    public ResponseEntity<QATestScenarioResponse> generateAiScenarios(@PathVariable Long requestId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scenarioService.generateAiScenarios(requestId));
    }
}
