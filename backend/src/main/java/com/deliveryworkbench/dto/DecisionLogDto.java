package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.DecisionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class DecisionLogDto {
    private Long id;
    private Long requestId;
    private String decisionTitle;
    private String decisionDescription;
    private DecisionType decisionType;
    private String decidedBy;
    private LocalDate decisionDate;
    private String impact;
    private OffsetDateTime createdAt;
}
