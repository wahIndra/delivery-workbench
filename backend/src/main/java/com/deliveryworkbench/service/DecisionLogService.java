package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.CreateDecisionLogRequest;
import com.deliveryworkbench.dto.DecisionLogDto;
import com.deliveryworkbench.entity.DecisionLog;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.repository.DecisionLogRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionLogService {

    private final DecisionLogRepository decisionLogRepository;
    private final DeliveryRequestRepository requestRepository;

    @Transactional
    public DecisionLogDto createDecisionLog(Long requestId, CreateDecisionLogRequest request) {
        DeliveryRequest deliveryRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        DecisionLog decisionLog = DecisionLog.builder()
                .request(deliveryRequest)
                .decisionTitle(request.getDecisionTitle())
                .decisionDescription(request.getDecisionDescription())
                .decisionType(request.getDecisionType())
                .decidedBy(request.getDecidedBy())
                .decisionDate(request.getDecisionDate())
                .impact(request.getImpact())
                .build();

        decisionLog = decisionLogRepository.save(decisionLog);
        log.info("Created decision log {} for request {}", decisionLog.getId(), requestId);
        return mapToDto(decisionLog);
    }

    @Transactional(readOnly = true)
    public List<DecisionLogDto> getDecisionLogsByRequestId(Long requestId) {
        return decisionLogRepository.findByRequest_IdOrderByDecisionDateDesc(requestId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private DecisionLogDto mapToDto(DecisionLog log) {
        return DecisionLogDto.builder()
                .id(log.getId())
                .requestId(log.getRequest().getId())
                .decisionTitle(log.getDecisionTitle())
                .decisionDescription(log.getDecisionDescription())
                .decisionType(log.getDecisionType())
                .decidedBy(log.getDecidedBy())
                .decisionDate(log.getDecisionDate())
                .impact(log.getImpact())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
