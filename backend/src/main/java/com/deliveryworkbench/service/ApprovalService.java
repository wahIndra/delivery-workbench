package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.ApprovalDto;
import com.deliveryworkbench.dto.CreateApprovalRequest;
import com.deliveryworkbench.dto.ProcessApprovalRequest;
import com.deliveryworkbench.entity.Approval;
import com.deliveryworkbench.entity.ApprovalStatus;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.repository.ApprovalRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final DeliveryRequestRepository requestRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<ApprovalDto> getApprovalsForRequest(Long requestId) {
        return approvalRepository.findByRequest_IdOrderByCreatedAtDesc(requestId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApprovalDto createApprovalRequest(Long requestId, CreateApprovalRequest requestDto) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        // Check if there is already a pending approval for this type
        boolean exists = approvalRepository.existsByRequest_IdAndApprovalTypeAndStatus(
                requestId, requestDto.getApprovalType(), ApprovalStatus.PENDING);
                
        if (exists) {
            throw new BusinessRuleViolationException("A pending approval of type " + requestDto.getApprovalType() + " already exists.");
        }

        Approval approval = Approval.builder()
                .request(request)
                .approvalType(requestDto.getApprovalType())
                .approverRole(requestDto.getApproverRole())
                .status(ApprovalStatus.PENDING)
                .build();

        approval = approvalRepository.save(approval);
        
        // Trigger notification to the required role
        String roleTarget = "ROLE_" + requestDto.getApproverRole();
        notificationService.createNotification(
                roleTarget, 
                request, 
                com.deliveryworkbench.entity.NotificationType.APPROVAL_REQUIRED, 
                "Approval Required: " + requestDto.getApprovalType(), 
                "Your approval is requested for request " + request.getRequestCode()
        );
        
        return mapToDto(approval);
    }

    @Transactional
    public ApprovalDto processApproval(Long requestId, Long approvalId, ProcessApprovalRequest requestDto) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval not found"));

        if (!approval.getRequest().getId().equals(requestId)) {
            throw new BusinessRuleViolationException("Approval does not belong to this request");
        }

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessRuleViolationException("Approval is not in PENDING state");
        }

        String currentUserRole = SecurityUtils.getCurrentUserRole();
        if (currentUserRole != null && currentUserRole.equals("MANAGEMENT")) {
            throw new BusinessRuleViolationException("Management viewer cannot approve or reject.");
        }

        if (requestDto.getStatus() == ApprovalStatus.APPROVED) {
            approval.setStatus(ApprovalStatus.APPROVED);
            approval.setApprovedAt(OffsetDateTime.now());
        } else if (requestDto.getStatus() == ApprovalStatus.REJECTED) {
            approval.setStatus(ApprovalStatus.REJECTED);
            approval.setRejectedAt(OffsetDateTime.now());
        } else {
            throw new BusinessRuleViolationException("Invalid target status. Must be APPROVED or REJECTED.");
        }

        approval.setApproverUser(SecurityUtils.getCurrentUsername());
        approval.setComment(requestDto.getComment());

        approval = approvalRepository.save(approval);
        return mapToDto(approval);
    }

    private ApprovalDto mapToDto(Approval approval) {
        return ApprovalDto.builder()
                .id(approval.getId())
                .requestId(approval.getRequest().getId())
                .approvalType(approval.getApprovalType())
                .approverRole(approval.getApproverRole())
                .approverUser(approval.getApproverUser())
                .status(approval.getStatus())
                .comment(approval.getComment())
                .approvedAt(approval.getApprovedAt())
                .rejectedAt(approval.getRejectedAt())
                .createdAt(approval.getCreatedAt())
                .build();
    }
}
