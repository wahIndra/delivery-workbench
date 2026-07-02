package com.deliveryworkbench.service;

import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.DeliveryStageHistory;
import com.deliveryworkbench.entity.ReadyStatus;
import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.repository.DefinitionOfReadyChecklistRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
import com.deliveryworkbench.repository.ReleaseReadinessRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Enforces business rules and records history for all state transitions (BR-01, BR-02, BR-06, BR-09, BR-10).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final DeliveryRequestRepository requestRepository;
    private final DeliveryStageHistoryRepository historyRepository;
    private final DefinitionOfReadyChecklistRepository dorRepository;
    private final ReleaseReadinessRepository releaseReadinessRepository;

    @Transactional
    public DeliveryRequest changeStatus(DeliveryRequest request, RequestStatus newStatus, String notes) {
        RequestStatus oldStatus = request.getStatus();
        
        if (oldStatus == newStatus) {
            return request;
        }

        validateTransition(request, oldStatus, newStatus);

        request.setStatus(newStatus);
        DeliveryRequest updatedRequest = requestRepository.save(request);

        recordHistory(updatedRequest, oldStatus, newStatus, notes);

        return updatedRequest;
    }

    private void validateTransition(DeliveryRequest request, RequestStatus oldStatus, RequestStatus newStatus) {
        // BR-09: businessOwner and itOwner required before READY_FOR_ANALYSIS
        if (newStatus == RequestStatus.READY_FOR_ANALYSIS) {
            if (request.getBusinessOwner() == null || request.getBusinessOwner().isBlank()) {
                throw new BusinessRuleViolationException("BR-09: Business Owner must be assigned before moving to READY_FOR_ANALYSIS");
            }
            if (request.getItOwner() == null || request.getItOwner().isBlank()) {
                throw new BusinessRuleViolationException("BR-09: IT Owner must be assigned before moving to READY_FOR_ANALYSIS");
            }
        }

        // BR-01: Cannot move to READY_FOR_DEVELOPMENT unless DoR readyStatus == READY
        if (newStatus == RequestStatus.READY_FOR_DEVELOPMENT) {
            boolean isReady = dorRepository.existsByRequest_IdAndReadyStatus(request.getId(), ReadyStatus.READY);
            if (!isReady) {
                throw new BusinessRuleViolationException("BR-01: Definition of Ready must be fully completed and approved before READY_FOR_DEVELOPMENT");
            }
        }

        // BR-02 and BR-10: Cannot move to READY_FOR_RELEASE unless ReleaseReadiness.readyForRelease == true and uatSignedOff == true
        if (newStatus == RequestStatus.READY_FOR_RELEASE) {
            boolean isReadyForRelease = releaseReadinessRepository.existsByRequest_IdAndReadyForReleaseTrue(request.getId());
            if (!isReadyForRelease) {
                throw new BusinessRuleViolationException("BR-02: Release Readiness checklist must be fully approved before READY_FOR_RELEASE");
            }
            
            boolean isUatSignedOff = releaseReadinessRepository.existsByRequest_IdAndUatSignedOffTrue(request.getId());
            if (!isUatSignedOff) {
                throw new BusinessRuleViolationException("BR-10: UAT must be signed off before READY_FOR_RELEASE");
            }
        }
    }

    public void recordHistory(DeliveryRequest request, RequestStatus fromStatus, RequestStatus toStatus, String notes) {
        // BR-06: Every status change recorded in DeliveryStageHistory
        String changedBy = SecurityUtils.getCurrentUsername();
        if (changedBy == null) {
            changedBy = "SYSTEM"; // Fallback if no auth context
        }

        DeliveryStageHistory history = DeliveryStageHistory.builder()
                .request(request)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .changedBy(changedBy)
                .notes(notes)
                .build();
                
        historyRepository.save(history);
        log.info("Request {} transitioned from {} to {} by {}", request.getRequestCode(), fromStatus, toStatus, changedBy);
    }
}
