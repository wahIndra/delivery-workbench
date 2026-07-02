package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.AIAuditLogResponse;
import com.deliveryworkbench.service.AIAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/ai-audit-logs")
@RequiredArgsConstructor
public class AIAuditLogController {

    private final AIAuditLogService aiAuditLogService;

    @GetMapping
    public ResponseEntity<List<AIAuditLogResponse>> getLogsByRequestId(@PathVariable Long requestId) {
        return ResponseEntity.ok(aiAuditLogService.getLogsByRequestId(requestId));
    }
}
