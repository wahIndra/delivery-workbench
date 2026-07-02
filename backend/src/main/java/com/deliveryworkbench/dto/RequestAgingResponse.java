package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.entity.SlaStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RequestAgingResponse {
    private Long requestId;
    private String requestCode;
    private String title;
    private RequestStatus currentStatus;
    private OffsetDateTime enteredStatusAt;
    private Integer agingHours;
    private Integer slaHours;
    private SlaStatus slaStatus;
}
