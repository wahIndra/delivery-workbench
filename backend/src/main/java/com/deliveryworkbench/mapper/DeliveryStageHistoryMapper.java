package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.DeliveryStageHistoryResponse;
import com.deliveryworkbench.entity.DeliveryStageHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryStageHistoryMapper {

    @Mapping(target = "requestId", source = "request.id")
    DeliveryStageHistoryResponse toResponse(DeliveryStageHistory history);
}
