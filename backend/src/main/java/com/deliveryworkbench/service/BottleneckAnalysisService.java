package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.BottleneckFindingResponse;
import com.deliveryworkbench.dto.UpdateFindingStatusRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.repository.BottleneckFindingRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
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
public class BottleneckAnalysisService {

    private final BottleneckFindingRepository findingRepository;
    private final DeliveryRequestRepository requestRepository;
    private final DeliveryStageHistoryRepository historyRepository;
    private final NotificationService notificationService;

    @Transactional
    public List<BottleneckFindingResponse> analyzeRequest(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        List<DeliveryStageHistory> history = historyRepository.findByRequest_IdOrderByChangedAtAsc(requestId);

        analyzeMissingOwner(request);
        analyzeRepeatedClarification(request, history);
        analyzeHighRework(request, history);

        return getFindingsForRequest(requestId);
    }

    private void analyzeMissingOwner(DeliveryRequest request) {
        if (request.getBusinessOwner() == null || request.getBusinessOwner().isBlank()) {
            createFindingIfNotExists(request, FindingType.MISSING_OWNER, FindingSeverity.HIGH,
                    "Business Owner is not assigned.",
                    "Assign a Business Owner to proceed effectively.");
        }
    }

    private void analyzeRepeatedClarification(DeliveryRequest request, List<DeliveryStageHistory> history) {
        long clarificationLoops = history.stream()
                .filter(h -> h.getToStatus() == RequestStatus.NEED_CLARIFICATION)
                .count();

        if (clarificationLoops > 2) {
            createFindingIfNotExists(request, FindingType.REPEATED_CLARIFICATION, FindingSeverity.MEDIUM,
                    "Request has been sent back for clarification " + clarificationLoops + " times.",
                    "Schedule a direct meeting with stakeholders to resolve all questions at once.");
        }
    }

    private void analyzeHighRework(DeliveryRequest request, List<DeliveryStageHistory> history) {
        long reworkCount = 0;
        for (DeliveryStageHistory h : history) {
            // Very simplified MVP check: If it went from a later stage back to an earlier stage
            // e.g., SIT -> IN_DEVELOPMENT or UAT -> IN_DEVELOPMENT
            if ((h.getFromStatus() == RequestStatus.SIT || h.getFromStatus() == RequestStatus.UAT) && 
                (h.getToStatus() == RequestStatus.IN_DEVELOPMENT)) {
                reworkCount++;
            }
        }

        if (reworkCount > 1) {
            createFindingIfNotExists(request, FindingType.HIGH_REWORK, FindingSeverity.CRITICAL,
                    "Code has been rejected and sent back to development " + reworkCount + " times.",
                    "Review code quality and verify unit test coverage before sending to QA again.");
        }
    }

    public void createFindingIfNotExists(DeliveryRequest request, FindingType type, FindingSeverity severity, String description, String recommendedAction) {
        boolean exists = findingRepository.existsByRequest_IdAndFindingTypeAndStatus(request.getId(), type, FindingStatus.OPEN);
        if (!exists) {
            BottleneckFinding finding = BottleneckFinding.builder()
                    .request(request)
                    .findingType(type)
                    .severity(severity)
                    .description(description)
                    .recommendedAction(recommendedAction)
                    .detectedBy(DetectedBy.SYSTEM)
                    .status(FindingStatus.OPEN)
                    .build();
            findingRepository.save(finding);
            log.info("Created new Bottleneck finding {} for Request {}", type, request.getRequestCode());
            
            // Trigger Notification
            if (request.getItOwner() != null) {
                notificationService.createNotification(
                        request.getItOwner(),
                        request,
                        com.deliveryworkbench.entity.NotificationType.BOTTLENECK_FOUND,
                        "Bottleneck Detected: " + type,
                        description
                );
            }
        }
    }

    public void logFinding(DeliveryRequest request, FindingType type, FindingSeverity severity, String description, String recommendedAction) {
        createFindingIfNotExists(request, type, severity, description, recommendedAction);
    }

    @Transactional(readOnly = true)
    public List<BottleneckFindingResponse> getFindingsForRequest(Long requestId) {
        return findingRepository.findByRequest_IdOrderByCreatedAtDesc(requestId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BottleneckFindingResponse> getActiveFindings() {
        return findingRepository.findByStatus(FindingStatus.OPEN).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BottleneckFindingResponse updateFindingStatus(Long findingId, UpdateFindingStatusRequest request) {
        BottleneckFinding finding = findingRepository.findById(findingId)
                .orElseThrow(() -> new ResourceNotFoundException("Finding not found"));

        finding.setStatus(request.getStatus());
        if (request.getStatus() == FindingStatus.RESOLVED || request.getStatus() == FindingStatus.IGNORED) {
            finding.setResolvedAt(OffsetDateTime.now());
        }

        BottleneckFinding updated = findingRepository.save(finding);
        return mapToResponse(updated);
    }

    private BottleneckFindingResponse mapToResponse(BottleneckFinding finding) {
        return BottleneckFindingResponse.builder()
                .id(finding.getId())
                .requestId(finding.getRequest().getId())
                .findingType(finding.getFindingType())
                .severity(finding.getSeverity())
                .description(finding.getDescription())
                .recommendedAction(finding.getRecommendedAction())
                .detectedBy(finding.getDetectedBy())
                .status(finding.getStatus())
                .createdAt(finding.getCreatedAt())
                .resolvedAt(finding.getResolvedAt())
                .build();
    }
}
