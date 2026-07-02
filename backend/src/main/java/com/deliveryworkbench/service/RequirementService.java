package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.dto.RequirementResponse;
import com.deliveryworkbench.dto.SaveRequirementRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.RequirementMapper;
import com.deliveryworkbench.repository.ClarificationQuestionRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequirementRepository;
import com.deliveryworkbench.repository.RequirementVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final RequirementVersionRepository requirementVersionRepository;
    private final DeliveryRequestRepository requestRepository;
    private final ClarificationQuestionRepository questionRepository;
    private final RequirementMapper requirementMapper;
    private final AIService aiService;
    private final AIAuditLogService aiAuditLogService;
    private final BottleneckAnalysisService bottleneckAnalysisService;

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
    public RequirementResponse saveRequirement(Long requestId, SaveRequirementRequest dto, String changedBy) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        Requirement req = requirementRepository.findTopByRequest_IdOrderByVersionDesc(requestId)
                .orElseGet(() -> createDefault(requestId));

        boolean isNewVersion = false;
        
        // If transitioning to APPROVED or explicitly provided a change reason, increment version
        if (("APPROVED".equals(dto.getStatus()) && !"APPROVED".equals(req.getStatus())) 
             || (dto.getChangeReason() != null && !dto.getChangeReason().trim().isEmpty())) {
            
            // Create a snapshot of current state before updating to new version
            snapshotVersion(req, dto.getChangeReason() != null ? dto.getChangeReason() : "Approved new version", changedBy);
            req.setVersion(req.getVersion() + 1);
            isNewVersion = true;
        }

        req.setScope(dto.getScope());
        req.setOutOfScope(dto.getOutOfScope());
        req.setUserStory(dto.getUserStory());
        req.setAcceptanceCriteria(dto.getAcceptanceCriteria());
        req.setAssumptions(dto.getAssumptions());
        req.setDependencies(dto.getDependencies());
        req.setStatus(dto.getStatus());

        req = requirementRepository.save(req);

        // Bottleneck rule: Requirement changes after READY_FOR_DEVELOPMENT implies potential HIGH_REWORK
        if (isNewVersion && request.getStatus().ordinal() >= RequestStatus.READY_FOR_DEVELOPMENT.ordinal() 
            && request.getStatus() != RequestStatus.CANCELLED && request.getStatus() != RequestStatus.RELEASED) {
            bottleneckAnalysisService.logFinding(request, FindingType.HIGH_REWORK, FindingSeverity.HIGH, 
                "Requirement was changed while request was in " + request.getStatus() + ". Version bumped to " + req.getVersion() + ".",
                "Review requirement changes and adjust development scope.");
        }

        return requirementMapper.toResponse(req);
    }

    private void snapshotVersion(Requirement req, String reason, String changedBy) {
        RequirementVersion version = RequirementVersion.builder()
                .requirement(req)
                .requestId(req.getRequest().getId())
                .version(req.getVersion())
                .scope(req.getScope())
                .outOfScope(req.getOutOfScope())
                .userStory(req.getUserStory())
                .acceptanceCriteria(req.getAcceptanceCriteria())
                .assumptions(req.getAssumptions())
                .dependencies(req.getDependencies())
                .changeReason(reason)
                .changedBy(changedBy)
                .build();
        requirementVersionRepository.save(version);
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

    @Transactional(readOnly = true)
    public List<com.deliveryworkbench.dto.RequirementVersionDto> getRequirementVersions(Long requestId) {
        return requirementVersionRepository.findByRequestIdOrderByVersionDesc(requestId).stream()
                .map(v -> com.deliveryworkbench.dto.RequirementVersionDto.builder()
                        .id(v.getId())
                        .requirementId(v.getRequirement().getId())
                        .requestId(v.getRequestId())
                        .version(v.getVersion())
                        .scope(v.getScope())
                        .outOfScope(v.getOutOfScope())
                        .userStory(v.getUserStory())
                        .acceptanceCriteria(v.getAcceptanceCriteria())
                        .assumptions(v.getAssumptions())
                        .dependencies(v.getDependencies())
                        .changeReason(v.getChangeReason())
                        .changedBy(v.getChangedBy())
                        .createdAt(v.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
