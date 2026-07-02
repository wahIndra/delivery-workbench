package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Request body for changing a delivery request's status. */
@Data
public class ChangeStatusRequest {

    @NotNull(message = "Target status is required")
    private RequestStatus toStatus;

    private String notes;
}
