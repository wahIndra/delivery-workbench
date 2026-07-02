package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.dto.RequirementResponse;
import com.deliveryworkbench.dto.SaveRequirementRequest;
import com.deliveryworkbench.entity.AIActionType;
import com.deliveryworkbench.entity.ClarificationQuestion;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.Requirement;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.RequirementMapper;
import com.deliveryworkbench.repository.ClarificationQuestionRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final DeliveryRequestRepository requestRepository;
    private final ClarificationQuestionRepository questionRepository;
    private final RequirementMapper requirementMapper;
    private final AIService aiService;
    private final AIAuditLogService aiAuditLogService;

    @Transactional(readOnly = true)
    public RequirementResponse getByRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId);
        }

        Requirement req = requirementRepository.findTopByRequest_IdOrderByVersionDesc(requestId)
                .orElseGet(() -> createDefault(requestId));

        return requirementMapper.toResponse(req);
    }

    @Transactional
    public RequirementResponse saveRequirement(Long requestId, SaveRequirementRequest dto) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        Requirement req = requirementRepository.findTopByRequest_IdOrderByVersionDesc(requestId)
                .orElseGet(() -> createDefault(requestId));

        // If transitioning to APPROVED, increment version
        if ("APPROVED".equals(dto.getStatus()) && !"APPROVED".equals(req.getStatus())) {
            req.setVersion(req.getVersion() + 1);
        }

        req.setScope(dto.getScope());
        req.setOutOfScope(dto.getOutOfScope());
        req.setUserStory(dto.getUserStory());
        req.setAcceptanceCriteria(dto.getAcceptanceCriteria());
        req.setAssumptions(dto.getAssumptions());
        req.setDependencies(dto.getDependencies());
        req.setStatus(dto.getStatus());

        req = requirementRepository.save(req);
        return requirementMapper.toResponse(req);
    }

    @Transactional
    public RequirementResponse generateAiRequirement(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        Requirement req = requirementRepository.findTopByRequest_IdOrderByVersionDesc(requestId)
                .orElseGet(() -> createDefault(requestId));

        // Gather clarifications for AI context
        List<ClarificationQuestion> questions = questionRepository.findByRequest_IdOrderByCreatedAtAsc(requestId);
        String clarificationSummary = questions.stream()
                .filter(q -> q.getAnswer() != null)
                .map(q -> "Q: " + q.getQuestion() + " | A: " + q.getAnswer())
                .collect(Collectors.joining("\n"));

        // Generate AI draft
        String aiOutput = aiService.generateUserStoryAndAcceptanceCriteria(
                request.getId(),
                request.getTitle(),
                request.getBusinessProblem(),
                clarificationSummary
        );

        // BR-03: Save output to AIAuditLog before returning
        String inputPrompt = String.format("Generate US & AC for: %s | Business Problem: %s | Clarifications: %s",
                request.getTitle(), request.getBusinessProblem(), clarificationSummary);
        
        aiAuditLogService.logAIAction(request, AIActionType.GENERATE_USER_STORY, inputPrompt, aiOutput);

        // BR-05: AI generates draft, human must approve. 
        // Here we just map the generated text into the fields. Since the output includes both, we can put it all in userStory for now,
        // or attempt to parse it. The mock returns a combined string. We'll store it in userStory for the human to split/edit.
        req.setUserStory(aiOutput);
        req.setStatus("DRAFT"); // Ensure it goes back to DRAFT for review

        req = requirementRepository.save(req);
        return requirementMapper.toResponse(req);
    }

    private Requirement createDefault(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        Requirement req = Requirement.builder()
                .request(request)
                .status("DRAFT")
                .version(1)
                .build();

        return requirementRepository.save(req);
    }
}
