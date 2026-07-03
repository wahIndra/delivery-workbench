package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.CreateReleaseScheduleRequest;
import com.deliveryworkbench.dto.ReleaseScheduleDto;
import com.deliveryworkbench.dto.UpdateReleaseScheduleRequest;
import com.deliveryworkbench.service.ReleaseScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReleaseScheduleController {

    private final ReleaseScheduleService releaseScheduleService;

    @PostMapping("/requests/{requestId}/release-schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER', 'SOLUTION_ARCHITECT', 'SYSTEM_ANALYST')")
    public ResponseEntity<ReleaseScheduleDto> createSchedule(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateReleaseScheduleRequest requestDto,
            Authentication authentication) {
        return ResponseEntity.ok(releaseScheduleService.createSchedule(requestId, requestDto, authentication.getName()));
    }

    @PutMapping("/release-schedules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER', 'SOLUTION_ARCHITECT')")
    public ResponseEntity<ReleaseScheduleDto> updateSchedule(
            @PathVariable Long id,
            @RequestBody UpdateReleaseScheduleRequest requestDto,
            Authentication authentication) {
        return ResponseEntity.ok(releaseScheduleService.updateSchedule(id, requestDto, authentication.getName()));
    }

    @GetMapping("/requests/{requestId}/release-schedules")
    @PreAuthorize("hasAnyRole('MANAGEMENT_VIEWER', 'ADMIN', 'RELEASE_MANAGER', 'SOLUTION_ARCHITECT', 'SYSTEM_ANALYST', 'QA', 'DEVELOPER')")
    public ResponseEntity<List<ReleaseScheduleDto>> getSchedulesByRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(releaseScheduleService.getSchedulesByRequest(requestId));
    }
    
    @GetMapping("/release-schedules")
    @PreAuthorize("hasAnyRole('MANAGEMENT_VIEWER', 'ADMIN', 'RELEASE_MANAGER', 'SOLUTION_ARCHITECT', 'SYSTEM_ANALYST', 'QA', 'DEVELOPER')")
    public ResponseEntity<List<ReleaseScheduleDto>> getAllSchedules() {
        return ResponseEntity.ok(releaseScheduleService.getAllSchedules());
    }
}
