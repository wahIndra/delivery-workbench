package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.DeliveryRequestResponse;
import com.deliveryworkbench.entity.DeliveryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AppUserMapper.class})
public interface DeliveryRequestMapper {

    @Mapping(target = "requester", source = "requester")
    DeliveryRequestResponse toResponse(DeliveryRequest request);
}
