package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.ApprovalDto;
import com.deliveryworkbench.dto.CreateApprovalRequest;
import com.deliveryworkbench.dto.ProcessApprovalRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.repository.ApprovalRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private DeliveryRequestRepository requestRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApprovalService approvalService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private DeliveryRequest request;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
        request = DeliveryRequest.builder().id(1L).build();
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void processApproval_ShouldRejectIfManagementViewer() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("MANAGEMENT");

        Approval approval = Approval.builder()
                .id(100L)
                .request(request)
                .status(ApprovalStatus.PENDING)
                .build();

        given(approvalRepository.findById(100L)).willReturn(Optional.of(approval));

        ProcessApprovalRequest req = new ProcessApprovalRequest();
        req.setStatus(ApprovalStatus.APPROVED);

        assertThatThrownBy(() -> approvalService.processApproval(1L, 100L, req))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Management viewer cannot approve or reject");
    }

    @Test
    void processApproval_ShouldApproveIfValid() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("SYSTEM_ANALYST");
        mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("sys.analyst");

        Approval approval = Approval.builder()
                .id(100L)
                .request(request)
                .status(ApprovalStatus.PENDING)
                .build();

        given(approvalRepository.findById(100L)).willReturn(Optional.of(approval));
        given(approvalRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        ProcessApprovalRequest req = new ProcessApprovalRequest();
        req.setStatus(ApprovalStatus.APPROVED);
        req.setComment("LGTM");

        ApprovalDto result = approvalService.processApproval(1L, 100L, req);

        assertThat(result.getStatus()).isEqualTo(ApprovalStatus.APPROVED);
        assertThat(result.getApproverUser()).isEqualTo("sys.analyst");
        assertThat(result.getComment()).isEqualTo("LGTM");
        assertThat(result.getApprovedAt()).isNotNull();
    }
}
