package com.deliveryworkbench.service;

import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.repository.ApprovalRepository;
import com.deliveryworkbench.repository.DefinitionOfReadyChecklistRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
import com.deliveryworkbench.repository.ReleaseReadinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for WorkflowService.
 * Tests BR-01 (DoR gate), BR-02/BR-10 (Release gate), BR-09 (Owner required), and Phase 9 Approvals.
 */
@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private DeliveryRequestRepository requestRepository;
    @Mock
    private DeliveryStageHistoryRepository historyRepository;
    @Mock
    private DefinitionOfReadyChecklistRepository dorRepository;
    @Mock
    private ReleaseReadinessRepository releaseReadinessRepository;
    @Mock
    private ApprovalRepository approvalRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private WorkflowService workflowService;

    private DeliveryRequest request;

    @BeforeEach
    void setUp() {
        request = DeliveryRequest.builder()
                .id(1L)
                .requestCode("REQ-TEST-001")
                .title("Test Request")
                .businessOwner("business.owner")
                .itOwner("system.analyst")
                .status(RequestStatus.SUBMITTED)
                .build();
    }

    // ── BR-01 Tests ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("BR-01: changeStatus to READY_FOR_DEVELOPMENT should throw when DoR is NOT_READY")
    void shouldThrowWhenDorNotReady() {
        given(dorRepository.existsByRequest_IdAndReadyStatus(1L, ReadyStatus.READY))
                .willReturn(false);

        assertThatThrownBy(() ->
                workflowService.changeStatus(request, RequestStatus.READY_FOR_DEVELOPMENT, "attempt"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("BR-01");
    }

    @Test
    @DisplayName("BR-01 & Phase 9: changeStatus to READY_FOR_DEVELOPMENT should succeed when DoR is READY and Solution Design approved")
    void shouldSucceedWhenDorReadyAndApproved() {
        given(dorRepository.existsByRequest_IdAndReadyStatus(1L, ReadyStatus.READY))
                .willReturn(true);
        given(approvalRepository.existsByRequest_IdAndApprovalTypeAndStatus(1L, ApprovalType.SOLUTION_DESIGN_APPROVAL, ApprovalStatus.APPROVED))
                .willReturn(true);
        given(requestRepository.save(any())).willReturn(request);

        workflowService.changeStatus(request, RequestStatus.READY_FOR_DEVELOPMENT, "approved");

        verify(requestRepository).save(request);
        verify(historyRepository).save(any());
    }

    // ── BR-02 / BR-10 / Phase 9 Tests ──────────────────────────────────────────────────

    @Test
    @DisplayName("BR-02: changeStatus to READY_FOR_RELEASE should throw when release readiness not approved")
    void shouldThrowWhenReleaseReadinessNotReady() {
        request.setStatus(RequestStatus.UAT);

        given(releaseReadinessRepository.existsByRequest_IdAndReadyForReleaseTrue(1L))
                .willReturn(false);

        assertThatThrownBy(() ->
                workflowService.changeStatus(request, RequestStatus.READY_FOR_RELEASE, "attempt"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("BR-02");
    }

    @Test
    @DisplayName("BR-10: changeStatus to READY_FOR_RELEASE should throw when UAT not signed off via Approval")
    void shouldThrowWhenUatNotSignedOff() {
        request.setStatus(RequestStatus.UAT);

        given(releaseReadinessRepository.existsByRequest_IdAndReadyForReleaseTrue(1L))
                .willReturn(true);
        given(approvalRepository.existsByRequest_IdAndApprovalTypeAndStatus(1L, ApprovalType.UAT_SIGNOFF, ApprovalStatus.APPROVED))
                .willReturn(false);

        assertThatThrownBy(() ->
                workflowService.changeStatus(request, RequestStatus.READY_FOR_RELEASE, "attempt"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("BR-10");
    }

    // ── BR-09 & Phase 9 Tests ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("BR-09: changeStatus to READY_FOR_ANALYSIS should throw when businessOwner is missing")
    void shouldThrowWhenBusinessOwnerMissing() {
        request.setBusinessOwner(null);

        assertThatThrownBy(() ->
                workflowService.changeStatus(request, RequestStatus.READY_FOR_ANALYSIS, "attempt"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("BR-09");
    }

    @Test
    @DisplayName("Phase 9: changeStatus to READY_FOR_ANALYSIS should throw when Requirement Signoff is missing")
    void shouldThrowWhenRequirementSignoffMissing() {
        given(approvalRepository.existsByRequest_IdAndApprovalTypeAndStatus(1L, ApprovalType.REQUIREMENT_SIGNOFF, ApprovalStatus.APPROVED))
                .willReturn(false);

        assertThatThrownBy(() ->
                workflowService.changeStatus(request, RequestStatus.READY_FOR_ANALYSIS, "attempt"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("REQUIREMENT_SIGNOFF");
    }

    @Test
    @DisplayName("No-op: changeStatus to same status should return request unchanged")
    void shouldReturnSameRequestWhenStatusUnchanged() {
        request.setStatus(RequestStatus.SUBMITTED);
        DeliveryRequest result = workflowService.changeStatus(request, RequestStatus.SUBMITTED, "no change");
        // No repository calls should be made, request returned as-is
        org.assertj.core.api.Assertions.assertThat(result.getStatus()).isEqualTo(RequestStatus.SUBMITTED);
    }
}
