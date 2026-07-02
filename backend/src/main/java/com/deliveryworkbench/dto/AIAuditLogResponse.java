package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.AIActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/** Response DTO for AI audit log entries — read-only. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAuditLogResponse {
    private Long id;
    private Long requestId;
    private String requestCode;
    private AIActionType aiActionType;
    private String inputPrompt;
    private String outputText;
    private String requestedBy;
    private OffsetDateTime createdAt;
}
