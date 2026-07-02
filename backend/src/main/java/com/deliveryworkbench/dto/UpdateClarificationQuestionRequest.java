package com.deliveryworkbench.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateClarificationQuestionRequest {

    @NotBlank(message = "Question is required")
    private String question;
}
