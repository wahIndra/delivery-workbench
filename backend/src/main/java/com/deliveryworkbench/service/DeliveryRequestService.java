package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.ChangeStatusRequest;
import com.deliveryworkbench.dto.CreateDeliveryRequestRequest;
import com.deliveryworkbench.dto.DeliveryRequestResponse;
import com.deliveryworkbench.dto.DeliveryStageHistoryResponse;
import com.deliveryworkbench.entity.AppUser;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.DeliveryStageHistory;
import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.DeliveryRequestMapper;
import com.deliveryworkbench.mapper.DeliveryStageHistoryMapper;
import com.deliveryworkbench.repository.AppUserRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryRequestService {

    private final DeliveryRequestRepository requestRepository;
    private final AppUserRepository userRepository;
    private final DeliveryStageHistoryRepository historyRepository;
    private final DeliveryRequestMapper requestMapper;
    private final DeliveryStageHistoryMapper historyMapper;
    private final WorkflowService workflowService;

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

        // Generate unique request code
        String requestCode = "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

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
                .status(RequestStatus.DRAFT) // Default to DRAFT
                .build();

        request = requestRepository.save(request);

        // Record initial history
        workflowService.recordHistory(request, null, RequestStatus.DRAFT, "Initial creation");

        return requestMapper.toResponse(request);
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
