package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessApprovalRequest {
    @NotNull(message = "Status is required (APPROVED or REJECTED)")
    private ApprovalStatus status;
    
    private String comment;
}
