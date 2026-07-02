package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.ai.NextBestActionRecommendation;
import com.deliveryworkbench.dto.NextBestActionResponse;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.repository.BottleneckFindingRepository;
import com.deliveryworkbench.repository.DefinitionOfReadyChecklistRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
import com.deliveryworkbench.repository.NextBestActionRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NextBestActionServiceTest {

    @Mock
    private NextBestActionRepository nbaRepository;
    @Mock
    private DeliveryRequestRepository requestRepository;
    @Mock
    private DeliveryStageHistoryRepository historyRepository;
    @Mock
    private BottleneckFindingRepository bottleneckRepository;
    @Mock
    private DefinitionOfReadyChecklistRepository dorRepository;
    @Mock
    private AIAuditLogService aiAuditLogService;
    @Mock
    private AIService aiService;

    @InjectMocks
    private NextBestActionService service;

    private DeliveryRequest request;

    @BeforeEach
    void setUp() {
        request = DeliveryRequest.builder()
                .id(1L)
                .requestCode("REQ-1")
                .status(RequestStatus.SUBMITTED)
                .build();
    }

    @Test
    void generateNextBestAction_ShouldCallAIAndAudit() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));
        given(historyRepository.findByRequest_IdOrderByChangedAtAsc(1L)).willReturn(List.of());
        given(bottleneckRepository.findByStatus(FindingStatus.OPEN)).willReturn(List.of());

        NextBestActionRecommendation rec = NextBestActionRecommendation.builder()
                .recommendation("Test action")
                .reason("Test reason")
                .build();

        given(aiService.generateNextBestAction(eq(1L), eq("SUBMITTED"), eq(false), eq(0L), eq(0L), any()))
                .willReturn(rec);

        NextBestAction savedNba = NextBestAction.builder()
                .id(100L)
                .request(request)
                .recommendation("Test action")
                .status(ActionStatus.PROPOSED)
                .source(ActionSource.AI)
                .build();
        given(nbaRepository.save(any())).willReturn(savedNba);

        NextBestActionResponse result = service.generateNextBestAction(1L, "user");

        assertThat(result.getRecommendation()).isEqualTo("Test action");
        verify(aiAuditLogService).logAIAction(eq(request), eq(AIActionType.GENERATE_NEXT_BEST_ACTION), any(), eq("Test action"));
    }
}
