package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.ai.NextBestActionRecommendation;
import com.deliveryworkbench.dto.NextBestActionResponse;
import com.deliveryworkbench.dto.UpdateNextBestActionRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NextBestActionService {

    private final NextBestActionRepository nbaRepository;
    private final DeliveryRequestRepository requestRepository;
    private final DeliveryStageHistoryRepository historyRepository;
    private final BottleneckFindingRepository bottleneckRepository;
    private final DefinitionOfReadyChecklistRepository dorRepository;
    private final AIAuditLogService aiAuditLogService;
    private final AIService aiService;

    @Transactional
    public NextBestActionResponse generateNextBestAction(Long requestId, String generatedBy) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        List<DeliveryStageHistory> history = historyRepository.findByRequest_IdOrderByChangedAtAsc(requestId);
        long clarificationCount = history.stream().filter(h -> h.getToStatus() == RequestStatus.NEED_CLARIFICATION).count();
        long openBottlenecks = bottleneckRepository.findByStatus(FindingStatus.OPEN).stream()
                .filter(b -> b.getRequest().getId().equals(requestId))
                .count();

        boolean isDorReady = dorRepository.findByRequest_Id(requestId)
                .map(dor -> dor.getReadyStatus() == ReadyStatus.READY)
                .orElse(false);

        String requestedBy = request.getRequester() != null ? request.getRequester().getUsername() : "Unknown";

        // 1. Generate via AI Service
        NextBestActionRecommendation aiRec = aiService.generateNextBestAction(
                requestId,
                request.getStatus() != null ? request.getStatus().name() : "SUBMITTED",
                isDorReady,
                clarificationCount,
                openBottlenecks,
                requestedBy
        );

        // 2. Persist to DB
        NextBestAction nba = NextBestAction.builder()
                .request(request)
                .recommendation(aiRec.getRecommendation())
                .reason(aiRec.getReason())
                .suggestedDueDate(OffsetDateTime.now().plusDays(3))
                .source(ActionSource.AI)
                .status(ActionStatus.PROPOSED)
                .build();
        
        nba = nbaRepository.save(nba);

        // 3. Log to AIAuditLog (BR-03)
        aiAuditLogService.logAIAction(
                request,
                AIActionType.GENERATE_NEXT_BEST_ACTION,
                "Context used: Status=" + request.getStatus() + ", Bottlenecks=" + openBottlenecks,
                aiRec.getRecommendation()
        );

        log.info("Generated Next Best Action for Request {}", request.getRequestCode());
        return mapToResponse(nba);
    }

    @Transactional(readOnly = true)
    public List<NextBestActionResponse> getActionsForRequest(Long requestId) {
        return nbaRepository.findByRequest_IdOrderByCreatedAtDesc(requestId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NextBestActionResponse updateActionStatus(Long actionId, UpdateNextBestActionRequest updateRequest) {
        NextBestAction action = nbaRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("NextBestAction not found"));

        action.setStatus(updateRequest.getStatus());
        
        if (updateRequest.getStatus() == ActionStatus.ACCEPTED) {
            action.setAcceptedAt(OffsetDateTime.now());
        } else if (updateRequest.getStatus() == ActionStatus.REJECTED) {
            action.setRejectedAt(OffsetDateTime.now());
        }

        return mapToResponse(nbaRepository.save(action));
    }

    private NextBestActionResponse mapToResponse(NextBestAction nba) {
        return NextBestActionResponse.builder()
                .id(nba.getId())
                .requestId(nba.getRequest().getId())
                .recommendation(nba.getRecommendation())
                .reason(nba.getReason())
                .suggestedOwner(nba.getSuggestedOwner())
                .suggestedDueDate(nba.getSuggestedDueDate())
                .source(nba.getSource())
                .status(nba.getStatus())
                .createdAt(nba.getCreatedAt())
                .acceptedAt(nba.getAcceptedAt())
                .rejectedAt(nba.getRejectedAt())
                .build();
    }
}
