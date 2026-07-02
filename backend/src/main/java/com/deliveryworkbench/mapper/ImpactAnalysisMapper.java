package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.ImpactAnalysisResponse;
import com.deliveryworkbench.entity.ImpactAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImpactAnalysisMapper {

    @Mapping(target = "requestId", source = "request.id")
    ImpactAnalysisResponse toResponse(ImpactAnalysis analysis);
}
