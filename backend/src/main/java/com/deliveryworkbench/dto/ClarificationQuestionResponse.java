package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.QuestionSource;
import com.deliveryworkbench.entity.QuestionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClarificationQuestionResponse {
    private Long id;
    private Long requestId;
    private String question;
    private String answer;
    private String askedBy;
    private String answeredBy;
    private QuestionSource source;
    private QuestionStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime answeredAt;
}
