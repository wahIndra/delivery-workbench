package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.dto.ImpactAnalysisResponse;
import com.deliveryworkbench.dto.SaveImpactAnalysisRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.ImpactAnalysisMapper;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.ImpactAnalysisRepository;
import com.deliveryworkbench.repository.RequirementRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImpactAnalysisService {

    private final ImpactAnalysisRepository impactAnalysisRepository;
    private final DeliveryRequestRepository requestRepository;
    private final RequirementRepository requirementRepository;
    private final ImpactAnalysisMapper impactAnalysisMapper;
    private final AIService aiService;
    private final AIAuditLogService aiAuditLogService;

    @Transactional(readOnly = true)
    public ImpactAnalysisResponse getByRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId);
        }

        ImpactAnalysis analysis = impactAnalysisRepository.findByRequest_Id(requestId)
                .orElseGet(() -> createDefault(requestId));

        return impactAnalysisMapper.toResponse(analysis);
    }

    @Transactional
    public ImpactAnalysisResponse saveImpactAnalysis(Long requestId, SaveImpactAnalysisRequest dto) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        ImpactAnalysis analysis = impactAnalysisRepository.findByRequest_Id(requestId)
                .orElseGet(() -> createDefault(requestId));

        String reviewer = SecurityUtils.getCurrentUsername();
        if (reviewer != null && dto.getStatus() == AnalysisStatus.APPROVED) {
            analysis.setReviewedBy(reviewer);
        }

        analysis.setImpactedApplications(dto.getImpactedApplications());
        analysis.setImpactedDatabases(dto.getImpactedDatabases());
        analysis.setImpactedApis(dto.getImpactedApis());
        analysis.setImpactedJobs(dto.getImpactedJobs());
        analysis.setImpactedQueues(dto.getImpactedQueues());
        analysis.setIntegrationImpact(dto.getIntegrationImpact());
        analysis.setSecurityImpact(dto.getSecurityImpact());
        analysis.setPerformanceImpact(dto.getPerformanceImpact());
        analysis.setOperationalImpact(dto.getOperationalImpact());
        analysis.setDataImpact(dto.getDataImpact());
        analysis.setRiskLevel(dto.getRiskLevel());
        analysis.setMitigationPlan(dto.getMitigationPlan());
        analysis.setStatus(dto.getStatus());

        analysis = impactAnalysisRepository.save(analysis);
        return impactAnalysisMapper.toResponse(analysis);
    }

    @Transactional
    public ImpactAnalysisResponse generateAiAnalysis(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        ImpactAnalysis analysis = impactAnalysisRepository.findByRequest_Id(requestId)
                .orElseGet(() -> createDefault(requestId));

        // Gather Requirement scope
        Requirement requirement = requirementRepository.findTopByRequest_IdOrderByVersionDesc(requestId)
                .orElse(null);
        String requirementScope = requirement != null && requirement.getUserStory() != null 
                ? requirement.getUserStory() 
                : request.getExpectedOutcome();

        // Generate AI draft
        String aiOutput = aiService.generateImpactAnalysisDraft(
                request.getId(),
                request.getTitle(),
                request.getImpactedSystems(),
                requirementScope
        );

        // BR-03: Save output to AIAuditLog before returning
        String inputPrompt = String.format("Generate Impact Analysis for: %s | Impacted Systems: %s | Scope: %s",
                request.getTitle(), request.getImpactedSystems(), requirementScope);
        
        aiAuditLogService.logAIAction(request, AIActionType.GENERATE_IMPACT_ANALYSIS_DRAFT, inputPrompt, aiOutput);

        // Save AI output to the analysis DRAFT
        // We'll store the AI draft in a general field or distribute if we parse it. For MVP, we can put it in impactedApplications as a big block, 
        // or just set integrationImpact. We'll dump it into integrationImpact for now to be edited.
        analysis.setIntegrationImpact(aiOutput);
        analysis.setStatus(AnalysisStatus.DRAFT);
        // AI does not set riskLevel (BR-05) - it's human set. Default is MEDIUM.

        analysis = impactAnalysisRepository.save(analysis);
        return impactAnalysisMapper.toResponse(analysis);
    }

    private ImpactAnalysis createDefault(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        ImpactAnalysis analysis = ImpactAnalysis.builder()
                .request(request)
                .status(AnalysisStatus.DRAFT)
                .build();

        return impactAnalysisRepository.save(analysis);
    }
}
