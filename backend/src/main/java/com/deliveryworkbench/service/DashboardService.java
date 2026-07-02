package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.DashboardMetricsResponse;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.DeliveryStageHistory;
import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DeliveryRequestRepository requestRepository;
    private final DeliveryStageHistoryRepository historyRepository;

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getDashboardMetrics() {
        List<DeliveryRequest> allRequests = requestRepository.findAll();
        List<DeliveryStageHistory> allHistory = historyRepository.findAll();

        // 1. Total requests by status
        Map<String, Long> statusCount = allRequests.stream()
                .collect(Collectors.groupingBy(r -> r.getStatus().name(), Collectors.counting()));

        // 8. Stuck requests (> 14 days in same status)
        Map<String, Long> stuckRequests = new HashMap<>();
        OffsetDateTime fourteenDaysAgo = OffsetDateTime.now().minusDays(14);
        for (DeliveryRequest req : allRequests) {
            if (req.getUpdatedAt().isBefore(fourteenDaysAgo) && req.getStatus() != RequestStatus.RELEASED && req.getStatus() != RequestStatus.CANCELLED) {
                stuckRequests.put(req.getStatus().name(), stuckRequests.getOrDefault(req.getStatus().name(), 0L) + 1);
            }
        }

        // 9. Aging requests (> 30 days overall)
        OffsetDateTime thirtyDaysAgo = OffsetDateTime.now().minusDays(30);
        long agingCount = allRequests.stream()
                .filter(r -> r.getCreatedAt().isBefore(thirtyDaysAgo) && r.getStatus() != RequestStatus.RELEASED && r.getStatus() != RequestStatus.CANCELLED)
                .count();

        // 10. Rework count (NEED_CLARIFICATION)
        long reworkCount = allHistory.stream()
                .filter(h -> h.getToStatus() == RequestStatus.NEED_CLARIFICATION)
                .count();

        // Averages (mocked or simple calc for MVP)
        // Group history by request
        Map<Long, List<DeliveryStageHistory>> historyByRequest = allHistory.stream()
                .collect(Collectors.groupingBy(h -> h.getRequest().getId()));

        double avgReqToReady = calculateAverageDuration(historyByRequest, RequestStatus.DRAFT, RequestStatus.READY_FOR_ANALYSIS);
        double avgReadyToDev = calculateAverageDuration(historyByRequest, RequestStatus.READY_FOR_ANALYSIS, RequestStatus.IN_DEVELOPMENT);
        double avgDevCycle = calculateAverageDuration(historyByRequest, RequestStatus.IN_DEVELOPMENT, RequestStatus.SIT);
        double avgSit = calculateAverageDuration(historyByRequest, RequestStatus.SIT, RequestStatus.UAT);
        double avgUat = calculateAverageDuration(historyByRequest, RequestStatus.UAT, RequestStatus.READY_FOR_RELEASE);
        double avgUatToRelease = calculateAverageDuration(historyByRequest, RequestStatus.READY_FOR_RELEASE, RequestStatus.RELEASED);

        return DashboardMetricsResponse.builder()
                .totalRequestsByStatus(statusCount)
                .avgRequestToReadyDays(avgReqToReady)
                .avgReadyToDevStartDays(avgReadyToDev)
                .avgDevCycleDays(avgDevCycle)
                .avgSitDays(avgSit)
                .avgUatDays(avgUat)
                .avgUatToReleaseDays(avgUatToRelease)
                .stuckRequestsByStage(stuckRequests)
                .totalAgingRequests(agingCount)
                .totalReworkCount(reworkCount)
                .build();
    }

    private double calculateAverageDuration(Map<Long, List<DeliveryStageHistory>> historyByRequest, RequestStatus startStatus, RequestStatus endStatus) {
        long totalSeconds = 0;
        int count = 0;

        for (List<DeliveryStageHistory> historyList : historyByRequest.values()) {
            OffsetDateTime startTime = null;
            OffsetDateTime endTime = null;

            // Find first occurrence of startStatus
            for (DeliveryStageHistory h : historyList) {
                if (h.getToStatus() == startStatus && startTime == null) {
                    startTime = h.getChangedAt();
                }
                if (h.getToStatus() == endStatus && endTime == null) {
                    endTime = h.getChangedAt();
                }
            }

            if (startTime != null && endTime != null && endTime.isAfter(startTime)) {
                totalSeconds += Duration.between(startTime, endTime).getSeconds();
                count++;
            }
        }

        if (count == 0) return 0.0;
        double avgSeconds = (double) totalSeconds / count;
        return Math.round((avgSeconds / (24 * 3600)) * 10.0) / 10.0; // round to 1 decimal place of days
    }
}
