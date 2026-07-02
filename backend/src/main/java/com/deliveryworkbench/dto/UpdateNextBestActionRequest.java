package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ActionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateNextBestActionRequest {
    @NotNull(message = "Status cannot be null")
    private ActionStatus status;
}
