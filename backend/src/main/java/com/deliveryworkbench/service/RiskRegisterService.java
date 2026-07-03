package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.CreateRiskRegisterRequest;
import com.deliveryworkbench.dto.RiskRegisterDto;
import com.deliveryworkbench.dto.UpdateRiskRegisterRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RiskRegisterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskRegisterService {

    private final RiskRegisterRepository riskRegisterRepository;
    private final DeliveryRequestRepository requestRepository;

    @Transactional
    public RiskRegisterDto createRisk(Long requestId, CreateRiskRegisterRequest requestDto, String username) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        int score = calculateRiskScore(requestDto.getProbability(), requestDto.getImpact());
        
        if (score >= 6 && (requestDto.getMitigationPlan() == null || requestDto.getMitigationPlan().isBlank())) {
            throw new IllegalStateException("High risk requires a mitigation plan");
        }

        RiskRegister risk = RiskRegister.builder()
                .request(request)
                .riskTitle(requestDto.getRiskTitle())
                .riskDescription(requestDto.getRiskDescription())
                .riskCategory(requestDto.getRiskCategory())
                .probability(requestDto.getProbability())
                .impact(requestDto.getImpact())
                .riskScore(score)
                .mitigationPlan(requestDto.getMitigationPlan())
                .owner(requestDto.getOwner() != null ? requestDto.getOwner() : username)
                .status(RiskStatus.OPEN)
                .build();

        RiskRegister saved = riskRegisterRepository.save(risk);
        log.info("Created Risk Register ID {} for Request {}", saved.getId(), request.getRequestCode());
        return mapToDto(saved);
    }

    @Transactional
    public RiskRegisterDto updateRisk(Long riskId, UpdateRiskRegisterRequest requestDto) {
        RiskRegister risk = riskRegisterRepository.findById(riskId)
                .orElseThrow(() -> new IllegalArgumentException("Risk not found"));

        if (requestDto.getRiskTitle() != null) risk.setRiskTitle(requestDto.getRiskTitle());
        if (requestDto.getRiskDescription() != null) risk.setRiskDescription(requestDto.getRiskDescription());
        if (requestDto.getRiskCategory() != null) risk.setRiskCategory(requestDto.getRiskCategory());
        if (requestDto.getOwner() != null) risk.setOwner(requestDto.getOwner());
        if (requestDto.getMitigationPlan() != null) risk.setMitigationPlan(requestDto.getMitigationPlan());

        if (requestDto.getProbability() != null) risk.setProbability(requestDto.getProbability());
        if (requestDto.getImpact() != null) risk.setImpact(requestDto.getImpact());
        
        int score = calculateRiskScore(risk.getProbability(), risk.getImpact());
        risk.setRiskScore(score);

        if (score >= 6 && (risk.getMitigationPlan() == null || risk.getMitigationPlan().isBlank())) {
            throw new IllegalStateException("High risk requires a mitigation plan");
        }

        if (requestDto.getStatus() != null) {
            risk.setStatus(requestDto.getStatus());
        }

        RiskRegister saved = riskRegisterRepository.save(risk);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RiskRegisterDto> getRisksByRequest(Long requestId) {
        return riskRegisterRepository.findByRequestIdOrderByCreatedAtDesc(requestId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private int calculateRiskScore(RiskProbability probability, RiskImpact impact) {
        int p = switch (probability) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
        };
        int i = switch (impact) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
        };
        return p * i; // Score 1-9
    }

    public RiskRegisterDto mapToDto(RiskRegister r) {
        return RiskRegisterDto.builder()
                .id(r.getId())
                .requestId(r.getRequest().getId())
                .riskTitle(r.getRiskTitle())
                .riskDescription(r.getRiskDescription())
                .riskCategory(r.getRiskCategory())
                .probability(r.getProbability())
                .impact(r.getImpact())
                .riskScore(r.getRiskScore())
                .mitigationPlan(r.getMitigationPlan())
                .owner(r.getOwner())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
