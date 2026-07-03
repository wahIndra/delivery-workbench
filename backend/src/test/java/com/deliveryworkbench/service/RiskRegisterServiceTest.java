package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.CreateRiskRegisterRequest;
import com.deliveryworkbench.dto.RiskRegisterDto;
import com.deliveryworkbench.dto.UpdateRiskRegisterRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RiskRegisterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RiskRegisterServiceTest {

    @Mock
    private RiskRegisterRepository riskRegisterRepository;

    @Mock
    private DeliveryRequestRepository requestRepository;

    @InjectMocks
    private RiskRegisterService riskRegisterService;

    private DeliveryRequest mockRequest;
    private RiskRegister mockRisk;

    @BeforeEach
    void setUp() {
        mockRequest = DeliveryRequest.builder()
                .id(1L)
                .requestCode("REQ-001")
                .build();

        mockRisk = RiskRegister.builder()
                .id(10L)
                .request(mockRequest)
                .riskTitle("API Delay")
                .probability(RiskProbability.HIGH)
                .impact(RiskImpact.HIGH)
                .riskScore(9)
                .mitigationPlan("Have backup team")
                .status(RiskStatus.OPEN)
                .build();
    }

    @Test
    void testCreateRisk_Success() {
        CreateRiskRegisterRequest request = new CreateRiskRegisterRequest();
        request.setRiskTitle("API Delay");
        request.setProbability(RiskProbability.MEDIUM);
        request.setImpact(RiskImpact.LOW);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(mockRequest));
        
        RiskRegister saved = RiskRegister.builder()
                .id(11L)
                .request(mockRequest)
                .riskTitle("API Delay")
                .probability(RiskProbability.MEDIUM)
                .impact(RiskImpact.LOW)
                .riskScore(2) // 2 * 1
                .status(RiskStatus.OPEN)
                .build();
                
        when(riskRegisterRepository.save(any(RiskRegister.class))).thenReturn(saved);

        RiskRegisterDto dto = riskRegisterService.createRisk(1L, request, "user1");

        assertNotNull(dto);
        assertEquals(2, dto.getRiskScore());
    }

    @Test
    void testCreateRisk_HighRiskRequiresMitigation() {
        CreateRiskRegisterRequest request = new CreateRiskRegisterRequest();
        request.setRiskTitle("API Delay");
        request.setProbability(RiskProbability.HIGH);
        request.setImpact(RiskImpact.HIGH);
        request.setMitigationPlan(""); // Empty mitigation plan

        when(requestRepository.findById(1L)).thenReturn(Optional.of(mockRequest));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            riskRegisterService.createRisk(1L, request, "user1");
        });

        assertTrue(ex.getMessage().contains("High risk requires a mitigation plan"));
    }

    @Test
    void testUpdateRisk_ScoreRecalculated() {
        UpdateRiskRegisterRequest request = new UpdateRiskRegisterRequest();
        request.setProbability(RiskProbability.LOW);
        request.setImpact(RiskImpact.LOW);

        when(riskRegisterRepository.findById(10L)).thenReturn(Optional.of(mockRisk));
        when(riskRegisterRepository.save(any(RiskRegister.class))).thenReturn(mockRisk);

        RiskRegisterDto dto = riskRegisterService.updateRisk(10L, request);

        assertEquals(1, dto.getRiskScore());
    }
}
