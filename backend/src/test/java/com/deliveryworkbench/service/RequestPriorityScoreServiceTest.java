package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.dto.RequestPriorityScoreResponse;
import com.deliveryworkbench.dto.UpdateRequestPriorityScoreRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.mapper.RequestPriorityScoreMapper;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequestPriorityScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestPriorityScoreServiceTest {

    @Mock
    private RequestPriorityScoreRepository scoreRepository;
    @Mock
    private DeliveryRequestRepository requestRepository;
    @Mock
    private RequestPriorityScoreMapper scoreMapper;
    @Mock
    private AIService aiService;
    @Mock
    private AIAuditLogService aiAuditLogService;

    @InjectMocks
    private RequestPriorityScoreService priorityScoreService;

    private DeliveryRequest request;
    private RequestPriorityScore score;
    private RequestPriorityScoreResponse mappedResponse;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("business.owner", null, List.of())
        );

        request = DeliveryRequest.builder()
                .id(1L)
                .title("Test Request")
                .businessProblem("Problem")
                .expectedOutcome("Outcome")
                .build();

        score = RequestPriorityScore.builder()
                .id(1L)
                .request(request)
                .businessImpactScore(1)
                .urgencyScore(1)
                .regulatoryImpactScore(1)
                .customerImpactScore(1)
                .operationalRiskScore(1)
                .technicalComplexityScore(1)
                .dependencyScore(1)
                .build();

        mappedResponse = RequestPriorityScoreResponse.builder().totalScore(5).build();
    }

    @Test
    void shouldCalculateTotalScoreAndDeriveRecommendation() {
        given(scoreRepository.findByRequest_Id(1L)).willReturn(Optional.of(score));
        given(scoreRepository.save(any())).willReturn(score);
        given(scoreMapper.toResponse(any())).willReturn(mappedResponse);

        UpdateRequestPriorityScoreRequest updateRequest = new UpdateRequestPriorityScoreRequest();
        updateRequest.setBusinessImpactScore(5); // 5
        updateRequest.setUrgencyScore(5); // 10
        updateRequest.setRegulatoryImpactScore(5); // 15
        updateRequest.setCustomerImpactScore(5); // 20
        updateRequest.setOperationalRiskScore(5); // 25
        updateRequest.setTechnicalComplexityScore(1); // 24
        updateRequest.setDependencyScore(1); // 23
        updateRequest.setScoringNotes("Critical issue");

        priorityScoreService.updateScore(1L, updateRequest);

        // Verification done implicitly by checking the score state passed to save
        verify(scoreRepository).save(org.mockito.ArgumentMatchers.argThat(savedScore -> 
            savedScore.getTotalScore() == 23 && 
            savedScore.getPriorityRecommendation() == PriorityRecommendation.CRITICAL &&
            "business.owner".equals(savedScore.getScoredBy())
        ));
    }

    @Test
    void shouldGenerateAiRecommendation() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));
        given(scoreRepository.findByRequest_Id(1L)).willReturn(Optional.of(score));
        given(aiService.generatePriorityRecommendation(1L, "Test Request", "Problem", "Outcome"))
                .willReturn("RECOMMENDED PRIORITY: HIGH\\nNotes...");
        given(scoreRepository.save(any())).willReturn(score);
        given(scoreMapper.toResponse(any())).willReturn(mappedResponse);

        priorityScoreService.generateAiRecommendation(1L);

        verify(aiAuditLogService).logAIAction(any(), any(), any(), any());
        verify(scoreRepository).save(org.mockito.ArgumentMatchers.argThat(savedScore -> 
            savedScore.getPriorityRecommendation() == PriorityRecommendation.HIGH
        ));
    }
}
