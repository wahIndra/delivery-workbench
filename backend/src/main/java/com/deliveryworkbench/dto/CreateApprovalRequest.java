package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ApprovalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateApprovalRequest {
    @NotNull(message = "Approval type is required")
    private ApprovalType approvalType;
    
    @NotBlank(message = "Approver role is required")
    private String approverRole;
}
