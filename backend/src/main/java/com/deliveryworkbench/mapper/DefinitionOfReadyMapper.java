package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.DefinitionOfReadyResponse;
import com.deliveryworkbench.entity.DefinitionOfReadyChecklist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DefinitionOfReadyMapper {

    @Mapping(target = "requestId", source = "request.id")
    DefinitionOfReadyResponse toResponse(DefinitionOfReadyChecklist checklist);
}
