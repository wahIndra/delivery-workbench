package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.dto.QATestScenarioResponse;
import com.deliveryworkbench.dto.SaveQATestScenarioRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.QATestScenarioMapper;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.ImpactAnalysisRepository;
import com.deliveryworkbench.repository.QATestScenarioRepository;
import com.deliveryworkbench.repository.RequirementRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QATestScenarioService {

    private final QATestScenarioRepository scenarioRepository;
    private final DeliveryRequestRepository requestRepository;
    private final RequirementRepository requirementRepository;
    private final ImpactAnalysisRepository impactAnalysisRepository;
    private final QATestScenarioMapper scenarioMapper;
    private final AIService aiService;
    private final AIAuditLogService aiAuditLogService;

    @Transactional(readOnly = true)
    public List<QATestScenarioResponse> getScenariosByRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId);
        }
        return scenarioRepository.findByRequest_IdOrderByCreatedAtAsc(requestId)
                .stream()
                .map(scenarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public QATestScenarioResponse saveScenario(Long requestId, SaveQATestScenarioRequest dto, Long scenarioId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            throw new BusinessRuleViolationException("Must be authenticated to save a scenario");
        }

        QATestScenario scenario;
        if (scenarioId != null) {
            scenario = scenarioRepository.findById(scenarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Scenario not found with id: " + scenarioId));
        } else {
            scenario = QATestScenario.builder()
                    .request(request)
                    .createdBy(username)
                    .source(QuestionSource.HUMAN)
                    .build();
        }

        scenario.setScenarioName(dto.getScenarioName());
        scenario.setScenarioType(dto.getScenarioType());
        scenario.setPrecondition(dto.getPrecondition());
        scenario.setTestSteps(dto.getTestSteps());
        scenario.setExpectedResult(dto.getExpectedResult());
        scenario.setStatus(dto.getStatus());

        scenario = scenarioRepository.save(scenario);
        return scenarioMapper.toResponse(scenario);
    }

    @Transactional
    public void deleteScenario(Long scenarioId) {
        if (!scenarioRepository.existsById(scenarioId)) {
            throw new ResourceNotFoundException("Scenario not found with id: " + scenarioId);
        }
        scenarioRepository.deleteById(scenarioId);
    }

    @Transactional
    public QATestScenarioResponse generateAiScenarios(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        // Gather Requirement and Impact Analysis context
        Requirement requirement = requirementRepository.findTopByRequest_IdOrderByVersionDesc(requestId)
                .orElse(null);
        String requirementScope = requirement != null && requirement.getUserStory() != null 
                ? requirement.getUserStory() 
                : request.getExpectedOutcome();
        String acceptanceCriteria = requirement != null && requirement.getAcceptanceCriteria() != null
                ? requirement.getAcceptanceCriteria()
                : "None";

        ImpactAnalysis analysis = impactAnalysisRepository.findByRequest_Id(requestId).orElse(null);
        String riskLevel = analysis != null && analysis.getRiskLevel() != null 
                ? analysis.getRiskLevel().name() 
                : "UNKNOWN";

        // Generate AI draft
        String aiOutput = aiService.generateTestScenarios(
                request.getId(),
                requirementScope,
                acceptanceCriteria,
                riskLevel
        );

        // BR-03: Save output to AIAuditLog before returning
        String inputPrompt = String.format("Generate Test Scenarios for: %s | Scope: %s | Risk: %s",
                request.getTitle(), requirementScope, riskLevel);
        
        aiAuditLogService.logAIAction(request, AIActionType.GENERATE_TEST_SCENARIOS, inputPrompt, aiOutput);

        // BR-05: Save AI output as a single DRAFT scenario
        QATestScenario scenario = QATestScenario.builder()
                .request(request)
                .scenarioName("AI Generated Test Scenarios (Draft Block)")
                .scenarioType(ScenarioType.POSITIVE)
                .testSteps(aiOutput) // Dump full AI output into testSteps for review
                .createdBy("AI")
                .source(QuestionSource.AI)
                .status(ScenarioStatus.DRAFT)
                .build();

        scenario = scenarioRepository.save(scenario);
        return scenarioMapper.toResponse(scenario);
    }
}
