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

        // Group history by request (used in multiple calculations below)
        Map<Long, List<DeliveryStageHistory>> historyByRequest = allHistory.stream()
                .collect(Collectors.groupingBy(h -> h.getRequest().getId()));

        // 8. Stuck requests: requests that entered their current status > 14 days ago
        // Uses DeliveryStageHistory for accurate timing (not updatedAt which changes on any field edit)
        Map<String, Long> stuckRequests = new HashMap<>();
        OffsetDateTime fourteenDaysAgo = OffsetDateTime.now().minusDays(14);
        for (DeliveryRequest req : allRequests) {
            if (req.getStatus() == RequestStatus.RELEASED || req.getStatus() == RequestStatus.CANCELLED) {
                continue;
            }
            // Find the most recent entry into the current status from history
            List<DeliveryStageHistory> reqHistory = historyByRequest.getOrDefault(req.getId(), List.of());
            OffsetDateTime enteredCurrentStatus = reqHistory.stream()
                    .filter(h -> h.getToStatus() == req.getStatus())
                    .map(DeliveryStageHistory::getChangedAt)
                    .max(OffsetDateTime::compareTo)
                    .orElse(req.getCreatedAt());
            if (enteredCurrentStatus.isBefore(fourteenDaysAgo)) {
                String statusName = req.getStatus().name();
                stuckRequests.merge(statusName, 1L, Long::sum);
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

        // Averages (using pre-computed historyByRequest map)

        double avgReqToReady = calculateAverageDuration(historyByRequest, RequestStatus.DRAFT, RequestStatus.READY_FOR_ANALYSIS);
        double avgReadyToDev = calculateAverageDuration(historyByRequest, RequestStatus.READY_FOR_ANALYSIS, RequestStatus.IN_DEVELOPMENT);
        double avgDevCycle = calculateAverageDuration(historyByRequest, RequestStatus.IN_DEVELOPMENT, RequestStatus.SIT);
        double avgSit = calculateAverageDuration(historyByRequest, RequestStatus.SIT, RequestStatus.UAT);
        double avgUat = calculateAverageDuration(historyByRequest, RequestStatus.UAT, RequestStatus.READY_FOR_RELEASE);
        double avgUatToRelease = calculateAverageDuration(historyByRequest, RequestStatus.READY_FOR_RELEASE, RequestStatus.RELEASED);

        // 11. Average lead time by month
        Map<String, Double> leadTimeByMonth = new HashMap<>();
        Map<String, Integer> releasesByMonthCount = new HashMap<>();
        Map<String, Long> releasesByMonthTotalSec = new HashMap<>();

        for (DeliveryRequest req : allRequests) {
            if (req.getStatus() == RequestStatus.RELEASED) {
                List<DeliveryStageHistory> reqHistory = historyByRequest.getOrDefault(req.getId(), List.of());
                OffsetDateTime releasedAt = reqHistory.stream()
                        .filter(h -> h.getToStatus() == RequestStatus.RELEASED)
                        .map(DeliveryStageHistory::getChangedAt)
                        .max(OffsetDateTime::compareTo)
                        .orElse(req.getUpdatedAt());

                String monthKey = releasedAt.getYear() + "-" + String.format("%02d", releasedAt.getMonthValue());
                long leadTimeSeconds = Duration.between(req.getCreatedAt(), releasedAt).getSeconds();

                releasesByMonthTotalSec.merge(monthKey, leadTimeSeconds, Long::sum);
                releasesByMonthCount.merge(monthKey, 1, Integer::sum);
            }
        }
        releasesByMonthCount.forEach((month, count) -> {
            double avgDays = (double) releasesByMonthTotalSec.get(month) / count / (24 * 3600);
            leadTimeByMonth.put(month, Math.round(avgDays * 10.0) / 10.0);
        });

        // 12 & 13. Owner distributions
        Map<String, Long> requestsByBusinessOwner = allRequests.stream()
                .filter(r -> r.getBusinessOwner() != null && !r.getBusinessOwner().isBlank())
                .collect(Collectors.groupingBy(DeliveryRequest::getBusinessOwner, Collectors.counting()));

        Map<String, Long> requestsByItOwner = allRequests.stream()
                .filter(r -> r.getItOwner() != null && !r.getItOwner().isBlank())
                .collect(Collectors.groupingBy(DeliveryRequest::getItOwner, Collectors.counting()));

        // 14. Priority distribution
        Map<String, Long> requestsByPriority = allRequests.stream()
                .filter(r -> r.getPriority() != null)
                .collect(Collectors.groupingBy(r -> r.getPriority().name(), Collectors.counting()));

        // 15. Recent releases (top 5)
        List<Map<String, Object>> recentReleases = allRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.RELEASED)
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .limit(5)
                .map(r -> Map.of(
                        "id", (Object) r.getId(),
                        "requestCode", r.getRequestCode(),
                        "title", r.getTitle(),
                        "releasedAt", r.getUpdatedAt().toString()
                ))
                .collect(Collectors.toList());

        // 16. Upcoming releases (candidates)
        List<Map<String, Object>> upcomingReleases = allRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.READY_FOR_RELEASE)
                .sorted((a, b) -> a.getUpdatedAt().compareTo(b.getUpdatedAt()))
                .limit(5)
                .map(r -> Map.of(
                        "id", (Object) r.getId(),
                        "requestCode", r.getRequestCode(),
                        "title", r.getTitle(),
                        "updatedAt", r.getUpdatedAt().toString()
                ))
                .collect(Collectors.toList());

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
                .avgLeadTimeByMonth(leadTimeByMonth)
                .requestsByBusinessOwner(requestsByBusinessOwner)
                .requestsByItOwner(requestsByItOwner)
                .requestsByPriority(requestsByPriority)
                .recentReleases(recentReleases)
                .upcomingReleases(upcomingReleases)
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
