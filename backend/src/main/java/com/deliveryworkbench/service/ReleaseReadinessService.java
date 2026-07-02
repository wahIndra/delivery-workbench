package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.ReleaseReadinessResponse;
import com.deliveryworkbench.dto.UpdateReleaseReadinessRequest;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.ReleaseReadiness;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.ReleaseReadinessMapper;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.ReleaseReadinessRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ReleaseReadinessService {

    private final ReleaseReadinessRepository readinessRepository;
    private final DeliveryRequestRepository requestRepository;
    private final ReleaseReadinessMapper readinessMapper;

    @Transactional(readOnly = true)
    public ReleaseReadinessResponse getReadinessByRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId);
        }

        ReleaseReadiness readiness = readinessRepository.findByRequest_Id(requestId)
                .orElseGet(() -> createDefault(requestId));

        return readinessMapper.toResponse(readiness);
    }

    @Transactional
    public ReleaseReadinessResponse updateReadiness(Long requestId, UpdateReleaseReadinessRequest dto) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        ReleaseReadiness readiness = readinessRepository.findByRequest_Id(requestId)
                .orElseGet(() -> createDefault(requestId));

        // Note: BR-05 implies AI must never approve this. The Controller protects this via @PreAuthorize
        String reviewer = SecurityUtils.getCurrentUsername();
        if (reviewer == null) {
            throw new BusinessRuleViolationException("Must be authenticated to update Release Readiness");
        }

        // BR-02/BR-10: ensure all items are checked if readyForRelease is true
        if (dto.isReadyForRelease()) {
            if (!dto.isRequirementSignedOff() || !dto.isSolutionDesignApproved() || !dto.isCodeReviewed() ||
                !dto.isSitPassed() || !dto.isUatSignedOff() || !dto.isSecurityReviewed() ||
                !dto.isDbScriptReviewed() || !dto.isRollbackPlanAvailable() || !dto.isMonitoringPrepared() ||
                !dto.isReleaseNotePrepared() || !dto.isSupportPicAssigned()) {
                throw new BusinessRuleViolationException("Cannot set readyForRelease = true unless all checklist items are completed.");
            }
        }

        readiness.setRequirementSignedOff(dto.isRequirementSignedOff());
        readiness.setSolutionDesignApproved(dto.isSolutionDesignApproved());
        readiness.setCodeReviewed(dto.isCodeReviewed());
        readiness.setSitPassed(dto.isSitPassed());
        readiness.setUatSignedOff(dto.isUatSignedOff());
        readiness.setSecurityReviewed(dto.isSecurityReviewed());
        readiness.setDbScriptReviewed(dto.isDbScriptReviewed());
        readiness.setRollbackPlanAvailable(dto.isRollbackPlanAvailable());
        readiness.setMonitoringPrepared(dto.isMonitoringPrepared());
        readiness.setReleaseNotePrepared(dto.isReleaseNotePrepared());
        readiness.setSupportPicAssigned(dto.isSupportPicAssigned());

        if (dto.isReadyForRelease() && !readiness.isReadyForRelease()) {
            readiness.setReviewedBy(reviewer);
            readiness.setReviewedAt(OffsetDateTime.now());
        }
        
        readiness.setReadyForRelease(dto.isReadyForRelease());

        readiness = readinessRepository.save(readiness);
        return readinessMapper.toResponse(readiness);
    }

    private ReleaseReadiness createDefault(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        ReleaseReadiness readiness = ReleaseReadiness.builder()
                .request(request)
                .build();

        return readinessRepository.save(readiness);
    }
}
