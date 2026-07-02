package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.DeliveryStageHistoryResponse;
import com.deliveryworkbench.service.DeliveryStageHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/stage-history")
@RequiredArgsConstructor
public class DeliveryStageHistoryController {

    private final DeliveryStageHistoryService historyService;

    @GetMapping
    public ResponseEntity<List<DeliveryStageHistoryResponse>> getHistoryByRequestId(@PathVariable Long requestId) {
        return ResponseEntity.ok(historyService.getHistoryByRequestId(requestId));
    }
}
