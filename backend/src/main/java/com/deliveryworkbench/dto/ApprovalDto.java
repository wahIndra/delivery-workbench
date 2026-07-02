package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ApprovalStatus;
import com.deliveryworkbench.entity.ApprovalType;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ApprovalDto {
    private Long id;
    private Long requestId;
    private ApprovalType approvalType;
    private String approverRole;
    private String approverUser;
    private ApprovalStatus status;
    private String comment;
    private OffsetDateTime approvedAt;
    private OffsetDateTime rejectedAt;
    private OffsetDateTime createdAt;
}
