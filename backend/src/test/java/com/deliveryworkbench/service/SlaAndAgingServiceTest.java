package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.DashboardSlaMetricsResponse;
import com.deliveryworkbench.dto.RequestAgingResponse;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.entity.SlaStatus;
import com.deliveryworkbench.entity.StageSlaPolicy;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequestAgingSnapshotRepository;
import com.deliveryworkbench.repository.StageSlaPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SlaAndAgingServiceTest {

    @Mock
    private DeliveryRequestRepository requestRepository;
    @Mock
    private StageSlaPolicyRepository slaPolicyRepository;
    @Mock
    private RequestAgingSnapshotRepository snapshotRepository;

    @InjectMocks
    private SlaAndAgingService slaService;

    private DeliveryRequest breachedRequest;
    private DeliveryRequest warningRequest;
    private DeliveryRequest normalRequest;
    private StageSlaPolicy policy;

    @BeforeEach
    void setUp() {
        policy = StageSlaPolicy.builder()
                .stage(RequestStatus.IN_DEVELOPMENT)
                .slaHours(336)
                .warningThresholdHours(240)
                .escalationThresholdHours(504)
                .active(true)
                .build();

        // Entered status 600 hours ago -> BREACHED (>504)
        breachedRequest = DeliveryRequest.builder()
                .id(1L)
                .title("Breached Req")
                .status(RequestStatus.IN_DEVELOPMENT)
                .statusEnteredAt(OffsetDateTime.now().minusHours(600))
                .build();

        // Entered status 300 hours ago -> WARNING (>240 but <504)
        warningRequest = DeliveryRequest.builder()
                .id(2L)
                .title("Warning Req")
                .status(RequestStatus.IN_DEVELOPMENT)
                .statusEnteredAt(OffsetDateTime.now().minusHours(300))
                .build();

        // Entered status 100 hours ago -> NORMAL (<240)
        normalRequest = DeliveryRequest.builder()
                .id(3L)
                .title("Normal Req")
                .status(RequestStatus.IN_DEVELOPMENT)
                .statusEnteredAt(OffsetDateTime.now().minusHours(100))
                .build();
    }

    @Test
    void shouldCalculateAgingProperly() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(breachedRequest));
        given(slaPolicyRepository.findByStageAndActiveTrue(RequestStatus.IN_DEVELOPMENT))
                .willReturn(Optional.of(policy));

        RequestAgingResponse response = slaService.getAgingForRequest(1L);

        assertThat(response.getAgingHours()).isGreaterThanOrEqualTo(599);
        assertThat(response.getSlaStatus()).isEqualTo(SlaStatus.BREACHED);
    }

    @Test
    void shouldGetDashboardMetrics() {
        given(requestRepository.findAll()).willReturn(List.of(breachedRequest, warningRequest, normalRequest));
        given(slaPolicyRepository.findByStageAndActiveTrue(RequestStatus.IN_DEVELOPMENT))
                .willReturn(Optional.of(policy));

        DashboardSlaMetricsResponse response = slaService.getDashboardMetrics();

        assertThat(response.getBreachedRequests()).hasSize(1);
        assertThat(response.getWarningRequests()).hasSize(1);
        assertThat(response.getBreachedRequests().get(0).getTitle()).isEqualTo("Breached Req");
        assertThat(response.getAverageAgingByStage().get("IN_DEVELOPMENT")).isGreaterThan(300);
        assertThat(response.getOldestRequestsByStage()).hasSize(1);
        assertThat(response.getOldestRequestsByStage().get(0).getTitle()).isEqualTo("Breached Req");
    }
}
