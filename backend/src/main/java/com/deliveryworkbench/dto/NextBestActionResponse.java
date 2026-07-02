package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ActionSource;
import com.deliveryworkbench.entity.ActionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class NextBestActionResponse {
    private Long id;
    private Long requestId;
    private String recommendation;
    private String reason;
    private String suggestedOwner;
    private OffsetDateTime suggestedDueDate;
    private ActionSource source;
    private ActionStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime acceptedAt;
    private OffsetDateTime rejectedAt;
}
