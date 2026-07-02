package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.DashboardSlaMetricsResponse;
import com.deliveryworkbench.dto.RequestAgingResponse;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequestAgingSnapshotRepository;
import com.deliveryworkbench.repository.StageSlaPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlaAndAgingService {

    private final DeliveryRequestRepository requestRepository;
    private final StageSlaPolicyRepository slaPolicyRepository;
    private final RequestAgingSnapshotRepository snapshotRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public RequestAgingResponse getAgingForRequest(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        return calculateAging(request);
    }

    @Transactional(readOnly = true)
    public DashboardSlaMetricsResponse getDashboardMetrics() {
        List<DeliveryRequest> activeRequests = requestRepository.findAll().stream()
                .filter(r -> r.getStatus() != RequestStatus.RELEASED && r.getStatus() != RequestStatus.CANCELLED)
                .collect(Collectors.toList());

        List<RequestAgingResponse> allAgings = activeRequests.stream()
                .map(this::calculateAging)
                .collect(Collectors.toList());

        List<RequestAgingResponse> breached = allAgings.stream()
                .filter(a -> a.getSlaStatus() == SlaStatus.BREACHED)
                .collect(Collectors.toList());

        List<RequestAgingResponse> warnings = allAgings.stream()
                .filter(a -> a.getSlaStatus() == SlaStatus.WARNING)
                .collect(Collectors.toList());

        Map<String, Double> averageAgingByStage = allAgings.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCurrentStatus().name(),
                        Collectors.averagingInt(RequestAgingResponse::getAgingHours)
                ));

        // Get oldest request per stage
        Map<RequestStatus, Optional<RequestAgingResponse>> oldestMap = allAgings.stream()
                .collect(Collectors.groupingBy(
                        RequestAgingResponse::getCurrentStatus,
                        Collectors.maxBy(Comparator.comparing(RequestAgingResponse::getAgingHours))
                ));

        List<RequestAgingResponse> oldestList = oldestMap.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return DashboardSlaMetricsResponse.builder()
                .breachedRequests(breached)
                .warningRequests(warnings)
                .averageAgingByStage(averageAgingByStage)
                .oldestRequestsByStage(oldestList)
                .build();
    }

    private RequestAgingResponse calculateAging(DeliveryRequest request) {
        OffsetDateTime enteredAt = request.getStatusEnteredAt() != null ? request.getStatusEnteredAt() : request.getUpdatedAt();
        Duration duration = Duration.between(enteredAt, OffsetDateTime.now());
        
        // MVP: Simple hour calculation (24/7). 
        // In a real app, you might want business hours (e.g. subtracting weekends).
        int agingHours = (int) duration.toHours();

        Optional<StageSlaPolicy> policyOpt = slaPolicyRepository.findByStageAndActiveTrue(request.getStatus());

        Integer slaHours = null;
        SlaStatus status = SlaStatus.NORMAL;

        if (policyOpt.isPresent()) {
            StageSlaPolicy policy = policyOpt.get();
            slaHours = policy.getSlaHours();
            
            if (agingHours >= policy.getEscalationThresholdHours()) {
                status = SlaStatus.BREACHED;
            } else if (agingHours >= policy.getWarningThresholdHours()) {
                status = SlaStatus.WARNING;
            }
        }

        return RequestAgingResponse.builder()
                .requestId(request.getId())
                .requestCode(request.getRequestCode())
                .title(request.getTitle())
                .currentStatus(request.getStatus())
                .enteredStatusAt(enteredAt)
                .agingHours(agingHours)
                .slaHours(slaHours)
                .slaStatus(status)
                .build();
    }

    @Transactional
    public void runSlaChecksAndNotify() {
        List<DeliveryRequest> activeRequests = requestRepository.findAll().stream()
                .filter(r -> r.getStatus() != RequestStatus.RELEASED && r.getStatus() != RequestStatus.CANCELLED)
                .collect(Collectors.toList());

        for (DeliveryRequest request : activeRequests) {
            RequestAgingResponse aging = calculateAging(request);
            
            if (aging.getSlaStatus() == SlaStatus.WARNING || aging.getSlaStatus() == SlaStatus.BREACHED) {
                // Check latest snapshot to see if we already notified for this status in this stage
                Optional<RequestAgingSnapshot> latestSnapshot = snapshotRepository.findTopByRequest_IdOrderByCalculatedAtDesc(request.getId());
                
                boolean shouldNotify = false;
                if (latestSnapshot.isEmpty()) {
                    shouldNotify = true;
                } else {
                    RequestAgingSnapshot snap = latestSnapshot.get();
                    if (snap.getCurrentStatus() != request.getStatus() || snap.getSlaStatus() != aging.getSlaStatus()) {
                        // Only notify if we transitioned into this state for the first time in this stage
                        shouldNotify = true;
                    }
                }
                
                if (shouldNotify) {
                    // Trigger notification
                    if (request.getItOwner() != null) {
                        notificationService.createNotification(
                                request.getItOwner(),
                                request,
                                aging.getSlaStatus() == SlaStatus.BREACHED ? com.deliveryworkbench.entity.NotificationType.SLA_BREACH : com.deliveryworkbench.entity.NotificationType.SLA_WARNING,
                                "SLA " + aging.getSlaStatus() + ": " + request.getRequestCode(),
                                "Request is in " + aging.getSlaStatus() + " for stage " + request.getStatus()
                        );
                    }
                    
                    // Save snapshot
                    RequestAgingSnapshot newSnap = RequestAgingSnapshot.builder()
                            .request(request)
                            .currentStatus(request.getStatus())
                            .enteredStatusAt(request.getStatusEnteredAt() != null ? request.getStatusEnteredAt() : request.getUpdatedAt())
                            .agingHours(aging.getAgingHours())
                            .slaHours(aging.getSlaHours())
                            .slaStatus(aging.getSlaStatus())
                            .calculatedAt(OffsetDateTime.now())
                            .build();
                    snapshotRepository.save(newSnap);
                }
            }
        }
    }
}
