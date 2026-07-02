package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStageHistoryResponse {
    private Long id;
    private Long requestId;
    private RequestStatus fromStatus;
    private RequestStatus toStatus;
    private String changedBy;
    private String notes;
    private OffsetDateTime changedAt;
}
