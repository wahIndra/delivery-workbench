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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReleaseScheduleService {

    private final ReleaseScheduleRepository releaseScheduleRepository;
    private final DeliveryRequestRepository requestRepository;
    private final WorkflowService workflowService;

    @Transactional
    public ReleaseScheduleDto createSchedule(Long requestId, CreateReleaseScheduleRequest requestDto, String username) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        ReleaseSchedule schedule = ReleaseSchedule.builder()
                .request(request)
                .releaseTitle(requestDto.getReleaseTitle())
                .plannedReleaseDate(requestDto.getPlannedReleaseDate())
                .releaseWindow(requestDto.getReleaseWindow())
                .releaseManager(requestDto.getReleaseManager() != null ? requestDto.getReleaseManager() : username)
                .releaseStatus(ReleaseStatus.PLANNED)
                .build();

        ReleaseSchedule saved = releaseScheduleRepository.save(schedule);
        log.info("Created Release Schedule ID {} for Request {}", saved.getId(), request.getRequestCode());
        return mapToDto(saved);
    }

    @Transactional
    public ReleaseScheduleDto updateSchedule(Long scheduleId, UpdateReleaseScheduleRequest requestDto, String username) {
        ReleaseSchedule schedule = releaseScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        DeliveryRequest request = schedule.getRequest();

        if (requestDto.getReleaseTitle() != null) schedule.setReleaseTitle(requestDto.getReleaseTitle());
        if (requestDto.getPlannedReleaseDate() != null) schedule.setPlannedReleaseDate(requestDto.getPlannedReleaseDate());
        if (requestDto.getActualReleaseDate() != null) schedule.setActualReleaseDate(requestDto.getActualReleaseDate());
        if (requestDto.getReleaseWindow() != null) schedule.setReleaseWindow(requestDto.getReleaseWindow());
        if (requestDto.getReleaseManager() != null) schedule.setReleaseManager(requestDto.getReleaseManager());
        if (requestDto.getRollbackPlan() != null) schedule.setRollbackPlan(requestDto.getRollbackPlan());
        if (requestDto.getReleaseNotes() != null) schedule.setReleaseNotes(requestDto.getReleaseNotes());

        if (requestDto.getReleaseStatus() != null) {
            ReleaseStatus newStatus = requestDto.getReleaseStatus();
            if (newStatus == ReleaseStatus.READY && request.getStatus() != RequestStatus.READY_FOR_RELEASE) {
                throw new IllegalStateException("Cannot mark release as READY because request is not READY_FOR_RELEASE");
            }

            if (newStatus == ReleaseStatus.RELEASED && schedule.getReleaseStatus() != ReleaseStatus.RELEASED) {
                // Rule 3: Released request should update request status to RELEASED
                if (request.getStatus() != RequestStatus.RELEASED) {
                    workflowService.changeStatus(request, RequestStatus.RELEASED, "Status updated by Release Manager " + username);
                }
            }

            schedule.setReleaseStatus(newStatus);
        }

        ReleaseSchedule saved = releaseScheduleRepository.save(schedule);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReleaseScheduleDto> getSchedulesByRequest(Long requestId) {
        return releaseScheduleRepository.findByRequestIdOrderByPlannedReleaseDateAsc(requestId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReleaseScheduleDto> getAllSchedules() {
        return releaseScheduleRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ReleaseScheduleDto mapToDto(ReleaseSchedule s) {
        return ReleaseScheduleDto.builder()
                .id(s.getId())
                .requestId(s.getRequest().getId())
                .releaseTitle(s.getReleaseTitle())
                .plannedReleaseDate(s.getPlannedReleaseDate())
                .actualReleaseDate(s.getActualReleaseDate())
                .releaseWindow(s.getReleaseWindow())
                .releaseManager(s.getReleaseManager())
                .releaseStatus(s.getReleaseStatus())
                .rollbackPlan(s.getRollbackPlan())
                .releaseNotes(s.getReleaseNotes())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
