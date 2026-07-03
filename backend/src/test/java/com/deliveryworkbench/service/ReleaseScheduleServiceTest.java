package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.CreateReleaseScheduleRequest;
import com.deliveryworkbench.dto.ReleaseScheduleDto;
import com.deliveryworkbench.dto.UpdateReleaseScheduleRequest;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.ReleaseSchedule;
import com.deliveryworkbench.entity.ReleaseStatus;
import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.ReleaseScheduleRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReleaseScheduleServiceTest {

    @Mock
    private ReleaseScheduleRepository releaseScheduleRepository;

    @Mock
    private DeliveryRequestRepository requestRepository;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private ReleaseScheduleService releaseScheduleService;

    private DeliveryRequest mockRequest;
    private ReleaseSchedule mockSchedule;

    @BeforeEach
    void setUp() {
        mockRequest = DeliveryRequest.builder()
                .id(1L)
                .requestCode("REQ-001")
                .status(RequestStatus.READY_FOR_RELEASE)
                .build();

        mockSchedule = ReleaseSchedule.builder()
                .id(100L)
                .request(mockRequest)
                .releaseTitle("V1.0 Release")
                .releaseStatus(ReleaseStatus.PLANNED)
                .build();
    }

    @Test
    void testCreateSchedule() {
        CreateReleaseScheduleRequest request = new CreateReleaseScheduleRequest();
        request.setReleaseTitle("V1.0 Release");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(mockRequest));
        when(releaseScheduleRepository.save(any(ReleaseSchedule.class))).thenReturn(mockSchedule);

        ReleaseScheduleDto dto = releaseScheduleService.createSchedule(1L, request, "testUser");

        assertNotNull(dto);
        assertEquals("V1.0 Release", dto.getReleaseTitle());
        assertEquals(ReleaseStatus.PLANNED, dto.getReleaseStatus());
    }

    @Test
    void testUpdateSchedule_MarkReadyFailsIfNotReadyForRelease() {
        mockRequest.setStatus(RequestStatus.UAT); // Not READY_FOR_RELEASE
        
        UpdateReleaseScheduleRequest request = new UpdateReleaseScheduleRequest();
        request.setReleaseStatus(ReleaseStatus.READY);

        when(releaseScheduleRepository.findById(100L)).thenReturn(Optional.of(mockSchedule));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            releaseScheduleService.updateSchedule(100L, request, "testUser");
        });

        assertTrue(ex.getMessage().contains("Cannot mark release as READY"));
    }

    @Test
    void testUpdateSchedule_MarkReleasedUpdatesRequestStatus() {
        mockRequest.setStatus(RequestStatus.READY_FOR_RELEASE);
        
        UpdateReleaseScheduleRequest request = new UpdateReleaseScheduleRequest();
        request.setReleaseStatus(ReleaseStatus.RELEASED);

        when(releaseScheduleRepository.findById(100L)).thenReturn(Optional.of(mockSchedule));
        when(releaseScheduleRepository.save(any(ReleaseSchedule.class))).thenReturn(mockSchedule);

        releaseScheduleService.updateSchedule(100L, request, "testUser");

        verify(workflowService, times(1)).changeStatus(mockRequest, RequestStatus.RELEASED, "Status updated by Release Manager testUser");
    }
}
