package com.deliveryworkbench.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SummarizeMeetingNoteRequest {
    @NotBlank(message = "Raw notes are required")
    private String rawNotes;
}
