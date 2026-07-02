package com.deliveryworkbench.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerClarificationQuestionRequest {

    @NotBlank(message = "Answer is required")
    private String answer;
}
