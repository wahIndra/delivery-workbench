package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.RequirementResponse;
import com.deliveryworkbench.dto.SaveRequirementRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.mapper.RequirementMapper;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequirementRepository;
import com.deliveryworkbench.repository.RequirementVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequirementServiceTest {

    @Mock
    private RequirementRepository requirementRepository;
    
    @Mock
    private RequirementVersionRepository requirementVersionRepository;
    
    @Mock
    private DeliveryRequestRepository requestRepository;
    
    @Mock
    private RequirementMapper requirementMapper;
    
    @Mock
    private BottleneckAnalysisService bottleneckAnalysisService;

    @InjectMocks
    private RequirementService service;

    private DeliveryRequest request;
    private Requirement requirement;

    @BeforeEach
    void setUp() {
        request = DeliveryRequest.builder().id(1L).status(RequestStatus.READY_FOR_DEVELOPMENT).build();
        requirement = Requirement.builder().id(100L).request(request).version(1).status("APPROVED").build();
    }

    @Test
    void saveRequirement_ShouldBumpVersionAndSnapshot_IfChangeReasonProvided() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));
        given(requirementRepository.findTopByRequest_IdOrderByVersionDesc(1L)).willReturn(Optional.of(requirement));
        
        Requirement updated = Requirement.builder().id(100L).request(request).version(2).status("APPROVED").build();
        given(requirementRepository.save(any())).willReturn(updated);
        
        RequirementResponse responseDto = RequirementResponse.builder().version(2).build();
        given(requirementMapper.toResponse(updated)).willReturn(responseDto);

        SaveRequirementRequest reqDto = new SaveRequirementRequest();
        reqDto.setStatus("APPROVED");
        reqDto.setChangeReason("Scope expansion");

        RequirementResponse result = service.saveRequirement(1L, reqDto, "user1");

        assertThat(result.getVersion()).isEqualTo(2);

        verify(requirementVersionRepository).save(any(RequirementVersion.class));
        verify(bottleneckAnalysisService).logFinding(eq(request), eq(FindingType.HIGH_REWORK), eq(FindingSeverity.HIGH), any(), any());
    }
}
