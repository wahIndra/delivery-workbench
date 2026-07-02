package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.UpdateReleaseReadinessRequest;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.ReleaseReadiness;
import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.mapper.ReleaseReadinessMapper;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.ReleaseReadinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for ReleaseReadinessService.
 * Tests BR-02: all checklist items must be true before readyForRelease can be set to true.
 */
@ExtendWith(MockitoExtension.class)
class ReleaseReadinessServiceTest {

    @Mock
    private ReleaseReadinessRepository readinessRepository;
    @Mock
    private DeliveryRequestRepository requestRepository;
    @Mock
    private ReleaseReadinessMapper readinessMapper;

    @InjectMocks
    private ReleaseReadinessService releaseReadinessService;

    private DeliveryRequest request;
    private ReleaseReadiness existingReadiness;

    @BeforeEach
    void setUp() {
        // Simulate authenticated user via Spring Security context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("release.manager", null, List.of())
        );

        request = DeliveryRequest.builder()
                .id(1L)
                .requestCode("REQ-TEST-001")
                .title("Test Request")
                .status(RequestStatus.UAT)
                .build();

        existingReadiness = ReleaseReadiness.builder()
                .id(1L)
                .request(request)
                .readyForRelease(false)
                .build();
    }

    @Test
    @DisplayName("BR-02: Should throw when trying to set readyForRelease=true with incomplete checklist")
    void shouldThrowWhenChecklistIncompleteAndReadyForReleaseTrue() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));
        given(readinessRepository.findByRequest_Id(1L)).willReturn(Optional.of(existingReadiness));

        // Only some items checked, but readyForRelease = true
        UpdateReleaseReadinessRequest dto = new UpdateReleaseReadinessRequest();
        dto.setRequirementSignedOff(true);
        dto.setSolutionDesignApproved(true);
        dto.setCodeReviewed(false); // not complete
        dto.setReadyForRelease(true); // attempting to approve

        assertThatThrownBy(() -> releaseReadinessService.updateReadiness(1L, dto))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("all checklist items");
    }

    @Test
    @DisplayName("BR-02: Should allow readyForRelease=true when all checklist items are true")
    void shouldAllowReadyForReleaseWhenAllItemsChecked() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));
        given(readinessRepository.findByRequest_Id(1L)).willReturn(Optional.of(existingReadiness));
        given(readinessRepository.save(any())).willReturn(existingReadiness);

        UpdateReleaseReadinessRequest dto = new UpdateReleaseReadinessRequest();
        dto.setRequirementSignedOff(true);
        dto.setSolutionDesignApproved(true);
        dto.setCodeReviewed(true);
        dto.setSitPassed(true);
        dto.setUatSignedOff(true);
        dto.setSecurityReviewed(true);
        dto.setDbScriptReviewed(true);
        dto.setRollbackPlanAvailable(true);
        dto.setMonitoringPrepared(true);
        dto.setReleaseNotePrepared(true);
        dto.setSupportPicAssigned(true);
        dto.setReadyForRelease(true);

        // Should not throw
        releaseReadinessService.updateReadiness(1L, dto);
    }
}
