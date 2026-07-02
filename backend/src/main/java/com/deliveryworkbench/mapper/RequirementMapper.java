package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.RequirementResponse;
import com.deliveryworkbench.entity.Requirement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequirementMapper {

    @Mapping(target = "requestId", source = "request.id")
    RequirementResponse toResponse(Requirement requirement);
}
