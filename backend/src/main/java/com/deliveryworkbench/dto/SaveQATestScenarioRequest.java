package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.ScenarioStatus;
import com.deliveryworkbench.entity.ScenarioType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveQATestScenarioRequest {

    @NotBlank(message = "Scenario name is required")
    private String scenarioName;

    @NotNull(message = "Scenario type is required")
    private ScenarioType scenarioType;

    private String precondition;
    private String testSteps;
    private String expectedResult;

    @NotNull(message = "Status is required")
    private ScenarioStatus status;
}
