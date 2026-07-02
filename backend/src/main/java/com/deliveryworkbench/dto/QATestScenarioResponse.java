package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ScenarioStatus;
import com.deliveryworkbench.entity.ScenarioType;
import com.deliveryworkbench.entity.QuestionSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QATestScenarioResponse {
    private Long id;
    private Long requestId;
    private String scenarioName;
    private ScenarioType scenarioType;
    private String precondition;
    private String testSteps;
    private String expectedResult;
    private ScenarioStatus status;
    private String createdBy;
    private QuestionSource source;
    private OffsetDateTime createdAt;
}
