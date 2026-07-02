package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.ApprovalDto;
import com.deliveryworkbench.dto.CreateApprovalRequest;
import com.deliveryworkbench.dto.ProcessApprovalRequest;
import com.deliveryworkbench.service.ApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping
    public ResponseEntity<List<ApprovalDto>> getApprovals(@PathVariable Long requestId) {
        return ResponseEntity.ok(approvalService.getApprovalsForRequest(requestId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'IT_OWNER', 'BUSINESS_OWNER', 'ADMIN')")
    public ResponseEntity<ApprovalDto> requestApproval(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateApprovalRequest request) {
        return ResponseEntity.ok(approvalService.createApprovalRequest(requestId, request));
    }

    @PutMapping("/{approvalId}")
    public ResponseEntity<ApprovalDto> processApproval(
            @PathVariable Long requestId,
            @PathVariable Long approvalId,
            @Valid @RequestBody ProcessApprovalRequest request) {
        return ResponseEntity.ok(approvalService.processApproval(requestId, approvalId, request));
    }
}
