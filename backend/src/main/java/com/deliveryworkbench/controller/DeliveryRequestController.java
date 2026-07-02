package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.ChangeStatusRequest;
import com.deliveryworkbench.dto.CreateDeliveryRequestRequest;
import com.deliveryworkbench.dto.DeliveryRequestResponse;
import com.deliveryworkbench.dto.DeliveryStageHistoryResponse;
import com.deliveryworkbench.entity.RequestStatus;
import com.deliveryworkbench.service.DeliveryRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class DeliveryRequestController {

    private final DeliveryRequestService deliveryRequestService;

    @GetMapping
    public ResponseEntity<Page<DeliveryRequestResponse>> searchRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(deliveryRequestService.searchRequests(status, keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryRequestResponse> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryRequestService.getRequestById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BUSINESS_USER', 'ADMIN')")
    public ResponseEntity<DeliveryRequestResponse> createRequest(
            @Valid @RequestBody CreateDeliveryRequestRequest request) {
        DeliveryRequestResponse created = deliveryRequestService.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('BUSINESS_USER', 'ADMIN')")
    public ResponseEntity<DeliveryRequestResponse> submitRequest(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryRequestService.submitRequest(id));
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'PRINCIPAL_ENGINEER', 'DEVELOPER', 'QA', 'RELEASE_MANAGER', 'ADMIN')")
    public ResponseEntity<DeliveryRequestResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request) {
        return ResponseEntity.ok(deliveryRequestService.changeStatus(id, request));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<DeliveryStageHistoryResponse>> getRequestHistory(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryRequestService.getRequestHistory(id));
    }
}
