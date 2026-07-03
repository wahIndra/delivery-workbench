package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.ChangeStatusRequest;
import com.deliveryworkbench.dto.CreateDeliveryRequestRequest;
import com.deliveryworkbench.dto.DeliveryRequestResponse;
import com.deliveryworkbench.dto.DeliveryStageHistoryResponse;
import com.deliveryworkbench.entity.AppUser;
import com.deliveryworkbench.entity.DefinitionOfReadyChecklist;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.DeliveryStageHistory;
import com.deliveryworkbench.entity.ImpactAnalysis;
import com.deliveryworkbench.entity.ReadyStatus;
import com.deliveryworkbench.entity.ReleaseReadiness;
import com.deliveryworkbench.entity.RequestPriorityScore;
import com.deliveryworkbench.entity.Requirement;
import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.DeliveryRequestMapper;
import com.deliveryworkbench.mapper.DeliveryStageHistoryMapper;
import com.deliveryworkbench.repository.AppUserRepository;
import com.deliveryworkbench.repository.DefinitionOfReadyChecklistRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
import com.deliveryworkbench.repository.ImpactAnalysisRepository;
import com.deliveryworkbench.repository.ReleaseReadinessRepository;
import com.deliveryworkbench.repository.RequestPriorityScoreRepository;
import com.deliveryworkbench.repository.RequirementRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryRequestService {

    private final DeliveryRequestRepository requestRepository;
    private final AppUserRepository userRepository;
    private final DeliveryStageHistoryRepository historyRepository;
    private final RequirementRepository requirementRepository;
    private final ImpactAnalysisRepository impactAnalysisRepository;
    private final DefinitionOfReadyChecklistRepository dorRepository;
    private final ReleaseReadinessRepository releaseReadinessRepository;
    private final RequestPriorityScoreRepository priorityScoreRepository;
    private final DeliveryRequestMapper requestMapper;
    private final DeliveryStageHistoryMapper historyMapper;
    private final WorkflowService workflowService;
    private final com.deliveryworkbench.integration.TicketingIntegrationService ticketingIntegrationService;

    @Transactional(readOnly = true)
    public Page<DeliveryRequestResponse> searchRequests(RequestStatus status, String keyword, Pageable pageable) {
        return requestRepository.search(status, keyword, pageable)
                .map(requestMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DeliveryRequestResponse getRequestById(Long id) {
        return requestRepository.findById(id)
                .map(requestMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + id));
    }

    @Transactional
    public DeliveryRequestResponse createRequest(CreateDeliveryRequestRequest dto) {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            throw new BusinessRuleViolationException("Must be authenticated to create a request");
        }

        AppUser requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // Generate year-sequential request code: REQ-YYYY-NNNNN
        String requestCode = generateRequestCode();

        DeliveryRequest request = DeliveryRequest.builder()
                .requestCode(requestCode)
                .title(dto.getTitle())
                .businessProblem(dto.getBusinessProblem())
                .expectedOutcome(dto.getExpectedOutcome())
                .currentProcess(dto.getCurrentProcess())
                .proposedChange(dto.getProposedChange())
                .impactedUsers(dto.getImpactedUsers())
                .impactedChannels(dto.getImpactedChannels())
                .impactedSystems(dto.getImpactedSystems())
                .priority(dto.getPriority())
                .deadline(dto.getDeadline())
                .deadlineReason(dto.getDeadlineReason())
                .businessOwner(dto.getBusinessOwner())
                .itOwner(dto.getItOwner())
                .uatPic(dto.getUatPic())
                .requester(requester)
                .status(RequestStatus.DRAFT)
                .build();

        request = requestRepository.save(request);

        // Record initial history
        workflowService.recordHistory(request, null, RequestStatus.DRAFT, "Initial creation");

        // Auto-create subordinate stubs so downstream pages don't 404 on a fresh request
        createSubordinateStubs(request);
        
        // Mock Integration: Simulate creating a ticket in Jira/ServiceNow
        ticketingIntegrationService.createTicket(request);

        return requestMapper.toResponse(request);
    }

    /**
     * Generates a unique, human-friendly request code in format REQ-YYYY-NNNNN.
     * Retries up to 10 times if there is a collision (extremely unlikely).
     */
    private String generateRequestCode() {
        int year = LocalDate.now().getYear();
        // Count existing requests to get a sequential number
        long count = requestRepository.count();
        for (int attempt = 0; attempt < 10; attempt++) {
            String code = String.format("REQ-%d-%05d", year, count + 1 + attempt);
            if (!requestRepository.existsByRequestCode(code)) {
                return code;
            }
        }
        // Fallback: append random suffix to guarantee uniqueness
        return String.format("REQ-%d-%05d-%s", year, count + 1,
                java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase());
    }

    /**
     * Creates default stubs for Requirement, ImpactAnalysis, DefinitionOfReady, ReleaseReadiness,
     * and RequestPriorityScore so downstream pages load immediately without 404 on a brand-new request.
     */
    private void createSubordinateStubs(DeliveryRequest request) {
        if (!requirementRepository.existsByRequest_Id(request.getId())) {
            requirementRepository.save(Requirement.builder().request(request).version(1).build());
        }
        if (!impactAnalysisRepository.existsByRequest_Id(request.getId())) {
            impactAnalysisRepository.save(ImpactAnalysis.builder().request(request).build());
        }
        if (!dorRepository.existsByRequest_Id(request.getId())) {
            dorRepository.save(DefinitionOfReadyChecklist.builder().request(request)
                    .readyStatus(ReadyStatus.NOT_READY).build());
        }
        if (!releaseReadinessRepository.existsByRequest_Id(request.getId())) {
            releaseReadinessRepository.save(ReleaseReadiness.builder().request(request).build());
        }
        if (!priorityScoreRepository.existsByRequest_Id(request.getId())) {
            priorityScoreRepository.save(RequestPriorityScore.builder().request(request).build());
        }
    }

    @Transactional
    public DeliveryRequestResponse submitRequest(Long id) {
        DeliveryRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + id));

        if (request.getStatus() != RequestStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only DRAFT requests can be submitted");
        }

        request = workflowService.changeStatus(request, RequestStatus.SUBMITTED, "Submitted by user");
        return requestMapper.toResponse(request);
    }

    @Transactional
    public DeliveryRequestResponse changeStatus(Long id, ChangeStatusRequest dto) {
        DeliveryRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + id));

        request = workflowService.changeStatus(request, dto.getToStatus(), dto.getNotes());
        return requestMapper.toResponse(request);
    }

    @Transactional(readOnly = true)
    public List<DeliveryStageHistoryResponse> getRequestHistory(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new ResourceNotFoundException("DeliveryRequest not found with id: " + id);
        }

        List<DeliveryStageHistory> history = historyRepository.findByRequest_IdOrderByChangedAtAsc(id);
        return history.stream()
                .map(historyMapper::toResponse)
                .collect(Collectors.toList());
    }
}
