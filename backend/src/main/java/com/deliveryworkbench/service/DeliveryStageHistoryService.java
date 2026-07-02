package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.DeliveryStageHistoryResponse;
import com.deliveryworkbench.mapper.DeliveryStageHistoryMapper;
import com.deliveryworkbench.repository.DeliveryStageHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryStageHistoryService {

    private final DeliveryStageHistoryRepository historyRepository;
    private final DeliveryStageHistoryMapper historyMapper;

    @Transactional(readOnly = true)
    public List<DeliveryStageHistoryResponse> getHistoryByRequestId(Long requestId) {
        return historyRepository.findByRequest_IdOrderByChangedAtAsc(requestId)
                .stream()
                .map(historyMapper::toResponse)
                .collect(Collectors.toList());
    }
}
