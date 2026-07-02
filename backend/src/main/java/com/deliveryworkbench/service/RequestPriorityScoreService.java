package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.dto.RequestPriorityScoreResponse;
import com.deliveryworkbench.dto.UpdateRequestPriorityScoreRequest;
import com.deliveryworkbench.entity.AIActionType;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.PriorityRecommendation;
import com.deliveryworkbench.entity.RequestPriorityScore;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.RequestPriorityScoreMapper;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequestPriorityScoreRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestPriorityScoreService {

    private final RequestPriorityScoreRepository scoreRepository;
    private final DeliveryRequestRepository requestRepository;
    private final RequestPriorityScoreMapper scoreMapper;
    private final AIService aiService;
    private final AIAuditLogService aiAuditLogService;

    @Transactional(readOnly = true)
    public RequestPriorityScoreResponse getScore(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId);
        }
        RequestPriorityScore score = scoreRepository.findByRequest_Id(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority Score not found for request id: " + requestId));
        return scoreMapper.toResponse(score);
    }

    @Transactional
    public RequestPriorityScoreResponse updateScore(Long requestId, UpdateRequestPriorityScoreRequest dto) {
        RequestPriorityScore score = scoreRepository.findByRequest_Id(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority Score not found for request id: " + requestId));

        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            throw new BusinessRuleViolationException("Must be authenticated to update priority score");
        }

        // Update fields
        score.setBusinessImpactScore(dto.getBusinessImpactScore());
        score.setUrgencyScore(dto.getUrgencyScore());
        score.setRegulatoryImpactScore(dto.getRegulatoryImpactScore());
        score.setCustomerImpactScore(dto.getCustomerImpactScore());
        score.setOperationalRiskScore(dto.getOperationalRiskScore());
        score.setTechnicalComplexityScore(dto.getTechnicalComplexityScore());
        score.setDependencyScore(dto.getDependencyScore());
        score.setScoringNotes(dto.getScoringNotes());
        score.setScoredBy(username);

        // Recalculate total
        int total = calculateTotalScore(score);
        score.setTotalScore(total);

        // Derive priority based on total score logic or simply let user/AI recommend it.
        // As per requirements: "Total score should be calculated from the scoring fields."
        // We will keep AI recommendation or set it automatically. 
        // For MVP, total score maps to recommendation if AI hasn't set one, or we just leave recommendation as is 
        // unless they click "Generate AI Recommendation". The logic below auto-assigns recommendation based on total.
        score.setPriorityRecommendation(deriveRecommendationFromTotal(total));

        score = scoreRepository.save(score);
        return scoreMapper.toResponse(score);
    }

    @Transactional
    public RequestPriorityScoreResponse generateAiRecommendation(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        RequestPriorityScore score = scoreRepository.findByRequest_Id(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority Score not found for request id: " + requestId));

        // Generate AI draft
        String aiOutput = aiService.generatePriorityRecommendation(
                request.getId(),
                request.getTitle(),
                request.getBusinessProblem(),
                request.getExpectedOutcome()
        );

        // Save output to AIAuditLog before returning
        String inputPrompt = String.format("Generate Priority Recommendation for: %s | Problem: %s | Outcome: %s",
                request.getTitle(), request.getBusinessProblem(), request.getExpectedOutcome());
        
        aiAuditLogService.logAIAction(request, AIActionType.GENERATE_PRIORITY_RECOMMENDATION, inputPrompt, aiOutput);

        // Parse recommendation from AI output for MVP (simple string matching)
        if (aiOutput.contains("RECOMMENDED PRIORITY: CRITICAL")) {
            score.setPriorityRecommendation(PriorityRecommendation.CRITICAL);
        } else if (aiOutput.contains("RECOMMENDED PRIORITY: HIGH")) {
            score.setPriorityRecommendation(PriorityRecommendation.HIGH);
        } else if (aiOutput.contains("RECOMMENDED PRIORITY: LOW")) {
            score.setPriorityRecommendation(PriorityRecommendation.LOW);
        } else {
            score.setPriorityRecommendation(PriorityRecommendation.MEDIUM);
        }
        
        score.setScoringNotes(aiOutput);

        score = scoreRepository.save(score);
        return scoreMapper.toResponse(score);
    }

    private int calculateTotalScore(RequestPriorityScore score) {
        // Formula: businessImpact + urgency + regulatory + customer + operational - technicalComplexity - dependency
        int total = score.getBusinessImpactScore() +
                    score.getUrgencyScore() +
                    score.getRegulatoryImpactScore() +
                    score.getCustomerImpactScore() +
                    score.getOperationalRiskScore() -
                    score.getTechnicalComplexityScore() -
                    score.getDependencyScore();
        return Math.max(1, total); // Ensure at least 1
    }

    private PriorityRecommendation deriveRecommendationFromTotal(int total) {
        if (total >= 20) return PriorityRecommendation.CRITICAL;
        if (total >= 15) return PriorityRecommendation.HIGH;
        if (total >= 10) return PriorityRecommendation.MEDIUM;
        return PriorityRecommendation.LOW;
    }
}
