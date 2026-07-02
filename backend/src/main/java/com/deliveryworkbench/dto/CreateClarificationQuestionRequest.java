package com.deliveryworkbench.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClarificationQuestionRequest {

    @NotBlank(message = "Question is required")
    private String question;
}
