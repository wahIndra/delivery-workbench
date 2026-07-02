package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.FindingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFindingStatusRequest {
    @NotNull(message = "Status cannot be null")
    private FindingStatus status;
}
