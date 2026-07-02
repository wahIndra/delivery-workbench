package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.CreateDecisionLogRequest;
import com.deliveryworkbench.dto.DecisionLogDto;
import com.deliveryworkbench.entity.DecisionLog;
import com.deliveryworkbench.entity.DecisionType;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.repository.DecisionLogRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DecisionLogServiceTest {

    @Mock
    private DecisionLogRepository decisionLogRepository;
    
    @Mock
    private DeliveryRequestRepository requestRepository;

    @InjectMocks
    private DecisionLogService service;

    private DeliveryRequest request;

    @BeforeEach
    void setUp() {
        request = DeliveryRequest.builder().id(1L).build();
    }

    @Test
    void createDecisionLog_ShouldCreateLog() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));

        CreateDecisionLogRequest req = new CreateDecisionLogRequest();
        req.setDecisionTitle("Test Title");
        req.setDecisionDescription("Desc");
        req.setDecisionType(DecisionType.TECHNICAL);
        req.setDecidedBy("user");
        req.setDecisionDate(LocalDate.now());

        DecisionLog savedLog = DecisionLog.builder()
                .id(100L)
                .request(request)
                .decisionTitle(req.getDecisionTitle())
                .decisionDescription(req.getDecisionDescription())
                .decisionType(req.getDecisionType())
                .decidedBy(req.getDecidedBy())
                .decisionDate(req.getDecisionDate())
                .createdAt(OffsetDateTime.now())
                .build();

        given(decisionLogRepository.save(any())).willReturn(savedLog);

        DecisionLogDto dto = service.createDecisionLog(1L, req);

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getDecisionTitle()).isEqualTo("Test Title");
        assertThat(dto.getDecisionType()).isEqualTo(DecisionType.TECHNICAL);
    }
}
