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
    private final com.deliveryworkbench.repository.ApprovalRepository approvalRepository;
    private final NotificationService notificationService;

    @Transactional
    public DeliveryRequest changeStatus(DeliveryRequest request, RequestStatus newStatus, String notes) {
        RequestStatus oldStatus = request.getStatus();
        
        if (oldStatus == newStatus) {
            return request;
        }

        validateTransition(request, oldStatus, newStatus);

        request.setStatus(newStatus);
        request.setStatusEnteredAt(java.time.OffsetDateTime.now());
        DeliveryRequest updatedRequest = requestRepository.save(request);

        recordHistory(updatedRequest, oldStatus, newStatus, notes);
        
        // Trigger notification
        if (request.getItOwner() != null) {
            notificationService.createNotification(
                    request.getItOwner(),
                    request,
                    com.deliveryworkbench.entity.NotificationType.STATUS_CHANGED,
                    "Status Changed to " + newStatus,
                    "Request " + request.getRequestCode() + " was moved from " + oldStatus + " to " + newStatus
            );
        }
        
        if (newStatus == RequestStatus.READY_FOR_RELEASE) {
            // Notify release manager role
            notificationService.createNotification(
                    "ROLE_ADMIN", // Release Manager is an ADMIN in our mock setup, or we could add a RELEASE_MANAGER role
                    request,
                    com.deliveryworkbench.entity.NotificationType.RELEASE_READY,
                    "Request Ready for Release",
                    "Request " + request.getRequestCode() + " is ready for release scheduling."
            );
        }

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
            
            // Phase 9: Requirement signoff required before READY_FOR_ANALYSIS
            boolean hasReqSignoff = approvalRepository.existsByRequest_IdAndApprovalTypeAndStatus(
                    request.getId(), com.deliveryworkbench.entity.ApprovalType.REQUIREMENT_SIGNOFF, com.deliveryworkbench.entity.ApprovalStatus.APPROVED);
            if (!hasReqSignoff) {
                throw new BusinessRuleViolationException("REQUIREMENT_SIGNOFF must be approved before moving to READY_FOR_ANALYSIS");
            }
        }

        // BR-01: Cannot move to READY_FOR_DEVELOPMENT unless DoR readyStatus == READY
        if (newStatus == RequestStatus.READY_FOR_DEVELOPMENT) {
            boolean isReady = dorRepository.existsByRequest_IdAndReadyStatus(request.getId(), ReadyStatus.READY);
            if (!isReady) {
                throw new BusinessRuleViolationException("BR-01: Definition of Ready must be fully completed and approved before READY_FOR_DEVELOPMENT");
            }
            
            // Phase 9: Solution design approval required before READY_FOR_DEVELOPMENT
            boolean hasDesignApproval = approvalRepository.existsByRequest_IdAndApprovalTypeAndStatus(
                    request.getId(), com.deliveryworkbench.entity.ApprovalType.SOLUTION_DESIGN_APPROVAL, com.deliveryworkbench.entity.ApprovalStatus.APPROVED);
            if (!hasDesignApproval) {
                throw new BusinessRuleViolationException("SOLUTION_DESIGN_APPROVAL must be approved before moving to READY_FOR_DEVELOPMENT");
            }
        }

        // BR-02 and BR-10: Cannot move to READY_FOR_RELEASE unless ReleaseReadiness.readyForRelease == true and uatSignedOff == true
        if (newStatus == RequestStatus.READY_FOR_RELEASE) {
            boolean isReadyForRelease = releaseReadinessRepository.existsByRequest_IdAndReadyForReleaseTrue(request.getId());
            if (!isReadyForRelease) {
                throw new BusinessRuleViolationException("BR-02: Release Readiness checklist must be fully approved before READY_FOR_RELEASE");
            }
            
            // Phase 9: Generic UAT_SIGNOFF required instead of (or in addition to) old boolean flag
            boolean isUatSignedOff = approvalRepository.existsByRequest_IdAndApprovalTypeAndStatus(
                    request.getId(), com.deliveryworkbench.entity.ApprovalType.UAT_SIGNOFF, com.deliveryworkbench.entity.ApprovalStatus.APPROVED);
            if (!isUatSignedOff) {
                throw new BusinessRuleViolationException("BR-10: UAT_SIGNOFF must be approved before moving to READY_FOR_RELEASE");
            }
        }
        
        // Phase 9: Release approval required before RELEASED
        if (newStatus == RequestStatus.RELEASED) {
            boolean hasReleaseApproval = approvalRepository.existsByRequest_IdAndApprovalTypeAndStatus(
                    request.getId(), com.deliveryworkbench.entity.ApprovalType.RELEASE_APPROVAL, com.deliveryworkbench.entity.ApprovalStatus.APPROVED);
            if (!hasReleaseApproval) {
                throw new BusinessRuleViolationException("RELEASE_APPROVAL must be approved before moving to RELEASED");
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
