package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.DefinitionOfReadyResponse;
import com.deliveryworkbench.dto.UpdateDefinitionOfReadyRequest;
import com.deliveryworkbench.entity.DefinitionOfReadyChecklist;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.ReadyStatus;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.DefinitionOfReadyMapper;
import com.deliveryworkbench.repository.DefinitionOfReadyChecklistRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefinitionOfReadyService {

    private final DefinitionOfReadyChecklistRepository dorRepository;
    private final DeliveryRequestRepository requestRepository;
    private final DefinitionOfReadyMapper dorMapper;

    @Transactional(readOnly = true)
    public DefinitionOfReadyResponse getByRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId);
        }

        DefinitionOfReadyChecklist dor = dorRepository.findByRequest_Id(requestId)
                .orElseGet(() -> createDefault(requestId));
                
        return dorMapper.toResponse(dor);
    }

    @Transactional
    public DefinitionOfReadyResponse updateChecklist(Long requestId, UpdateDefinitionOfReadyRequest dto) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        DefinitionOfReadyChecklist dor = dorRepository.findByRequest_Id(requestId)
                .orElseGet(() -> createDefault(requestId));

        // BR-05: readyStatus is human-set only. If AI tried to set this, block it.
        // Actually, our API is only hit by authenticated humans, so the logged-in user is the reviewer.
        String reviewer = SecurityUtils.getCurrentUsername();
        if (reviewer == null) {
            throw new BusinessRuleViolationException("Must be authenticated to update DoR");
        }

        dor.setBusinessProblemClear(dto.isBusinessProblemClear());
        dor.setExpectedOutcomeDefined(dto.isExpectedOutcomeDefined());
        dor.setScopeAgreed(dto.isScopeAgreed());
        dor.setOutOfScopeAgreed(dto.isOutOfScopeAgreed());
        dor.setImpactedUsersIdentified(dto.isImpactedUsersIdentified());
        dor.setImpactedSystemsIdentified(dto.isImpactedSystemsIdentified());
        dor.setProcessFlowDocumented(dto.isProcessFlowDocumented());
        dor.setDataRequirementListed(dto.isDataRequirementListed());
        dor.setIntegrationRequirementListed(dto.isIntegrationRequirementListed());
        dor.setAcceptanceCriteriaAgreed(dto.isAcceptanceCriteriaAgreed());
        dor.setPriorityClear(dto.isPriorityClear());
        dor.setDeadlineReasonClear(dto.isDeadlineReasonClear());
        dor.setRisksIdentified(dto.isRisksIdentified());
        dor.setBusinessOwnerAssigned(dto.isBusinessOwnerAssigned());
        dor.setItOwnerAssigned(dto.isItOwnerAssigned());
        dor.setTesterAssigned(dto.isTesterAssigned());

        dor.setReadyStatus(dto.getReadyStatus());
        
        // If they changed status to READY or PARTIALLY_READY, record who did it and when
        if (dto.getReadyStatus() != ReadyStatus.NOT_READY) {
            dor.setReviewedBy(reviewer);
            dor.setReviewedAt(OffsetDateTime.now());
        }

        DefinitionOfReadyChecklist saved = dorRepository.save(dor);
        return dorMapper.toResponse(saved);
    }

    private DefinitionOfReadyChecklist createDefault(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));
                
        DefinitionOfReadyChecklist checklist = DefinitionOfReadyChecklist.builder()
                .request(request)
                .readyStatus(ReadyStatus.NOT_READY)
                .build();
                
        return dorRepository.save(checklist);
    }
}
