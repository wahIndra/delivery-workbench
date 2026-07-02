package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.ReleaseReadinessResponse;
import com.deliveryworkbench.entity.ReleaseReadiness;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReleaseReadinessMapper {

    @Mapping(target = "requestId", source = "request.id")
    ReleaseReadinessResponse toResponse(ReleaseReadiness readiness);
}
