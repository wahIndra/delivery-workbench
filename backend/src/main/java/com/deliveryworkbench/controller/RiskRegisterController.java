package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.CreateRiskRegisterRequest;
import com.deliveryworkbench.dto.RiskRegisterDto;
import com.deliveryworkbench.dto.UpdateRiskRegisterRequest;
import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.ai.RiskSuggestionDto;
import com.deliveryworkbench.entity.AIActionType;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.service.AIAuditLogService;
import com.deliveryworkbench.service.RiskRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/risks")
@RequiredArgsConstructor
public class RiskRegisterController {

    private final RiskRegisterService riskRegisterService;
    private final AIService aiService;
    private final AIAuditLogService aiAuditLogService;
    private final DeliveryRequestRepository requestRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SOLUTION_ARCHITECT', 'SYSTEM_ANALYST', 'PROJECT_MANAGER', 'BUSINESS_OWNER')")
    public ResponseEntity<RiskRegisterDto> createRisk(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateRiskRegisterRequest requestDto,
            Authentication authentication) {
        return ResponseEntity.ok(riskRegisterService.createRisk(requestId, requestDto, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOLUTION_ARCHITECT', 'SYSTEM_ANALYST', 'PROJECT_MANAGER', 'BUSINESS_OWNER')")
    public ResponseEntity<RiskRegisterDto> updateRisk(
            @PathVariable Long requestId,
            @PathVariable Long id,
            @RequestBody UpdateRiskRegisterRequest requestDto) {
        return ResponseEntity.ok(riskRegisterService.updateRisk(id, requestDto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGEMENT_VIEWER', 'ADMIN', 'SOLUTION_ARCHITECT', 'SYSTEM_ANALYST', 'PROJECT_MANAGER', 'QA', 'DEVELOPER', 'BUSINESS_OWNER')")
    public ResponseEntity<List<RiskRegisterDto>> getRisksByRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(riskRegisterService.getRisksByRequest(requestId));
    }

    @GetMapping("/ai-suggestions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOLUTION_ARCHITECT', 'SYSTEM_ANALYST', 'PROJECT_MANAGER')")
    public ResponseEntity<List<RiskSuggestionDto>> generateRiskSuggestions(@PathVariable Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
                
        List<RiskSuggestionDto> suggestions = aiService.generateRiskSuggestions(
                requestId,
                request.getTitle(),
                request.getBusinessProblem()
        );
        
        // Log the AI action
        String inputPrompt = String.format("Request: %s, Problem: %s", request.getTitle(), request.getBusinessProblem());
        String outputText = suggestions.toString(); // Simple serialization for audit
        aiAuditLogService.logAIAction(request, AIActionType.GENERATE_RISK_SUGGESTIONS, inputPrompt, outputText);
        
        return ResponseEntity.ok(suggestions);
    }
}
