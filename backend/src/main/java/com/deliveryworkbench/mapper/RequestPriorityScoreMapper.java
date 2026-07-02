package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.RequestPriorityScoreResponse;
import com.deliveryworkbench.entity.RequestPriorityScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestPriorityScoreMapper {
    @Mapping(target = "requestId", source = "request.id")
    RequestPriorityScoreResponse toResponse(RequestPriorityScore entity);
}
