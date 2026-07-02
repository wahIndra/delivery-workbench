package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.BottleneckFindingResponse;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.repository.BottleneckFindingRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BottleneckAnalysisServiceTest {

    @Mock
    private BottleneckFindingRepository findingRepository;
    @Mock
    private DeliveryRequestRepository requestRepository;
    @Mock
    private DeliveryStageHistoryRepository historyRepository;

    @InjectMocks
    private BottleneckAnalysisService service;

    private DeliveryRequest requestMissingOwner;
    private DeliveryRequest requestWithRework;

    @BeforeEach
    void setUp() {
        requestMissingOwner = DeliveryRequest.builder()
                .id(1L)
                .requestCode("REQ-1")
                .businessOwner(null)
                .build();

        requestWithRework = DeliveryRequest.builder()
                .id(2L)
                .requestCode("REQ-2")
                .businessOwner("owner")
                .build();
    }

    @Test
    void analyzeRequest_ShouldDetectMissingOwner() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(requestMissingOwner));
        given(historyRepository.findByRequest_IdOrderByChangedAtAsc(1L)).willReturn(List.of());
        given(findingRepository.existsByRequest_IdAndFindingTypeAndStatus(1L, FindingType.MISSING_OWNER, FindingStatus.OPEN))
                .willReturn(false);

        service.analyzeRequest(1L);

        verify(findingRepository).save(any(BottleneckFinding.class));
    }

    @Test
    void analyzeRequest_ShouldDetectHighRework() {
        given(requestRepository.findById(2L)).willReturn(Optional.of(requestWithRework));
        
        DeliveryStageHistory h1 = DeliveryStageHistory.builder().fromStatus(RequestStatus.SIT).toStatus(RequestStatus.IN_DEVELOPMENT).build();
        DeliveryStageHistory h2 = DeliveryStageHistory.builder().fromStatus(RequestStatus.UAT).toStatus(RequestStatus.IN_DEVELOPMENT).build();
        
        given(historyRepository.findByRequest_IdOrderByChangedAtAsc(2L)).willReturn(List.of(h1, h2));
        given(findingRepository.existsByRequest_IdAndFindingTypeAndStatus(2L, FindingType.HIGH_REWORK, FindingStatus.OPEN))
                .willReturn(false);

        service.analyzeRequest(2L);

        verify(findingRepository).save(any(BottleneckFinding.class));
    }

    @Test
    void analyzeRequest_ShouldNotDuplicateOpenFindings() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(requestMissingOwner));
        given(historyRepository.findByRequest_IdOrderByChangedAtAsc(1L)).willReturn(List.of());
        given(findingRepository.existsByRequest_IdAndFindingTypeAndStatus(1L, FindingType.MISSING_OWNER, FindingStatus.OPEN))
                .willReturn(true); // Already exists

        service.analyzeRequest(1L);

        verify(findingRepository, never()).save(any(BottleneckFinding.class));
    }
}
