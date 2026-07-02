package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.QATestScenarioResponse;
import com.deliveryworkbench.entity.QATestScenario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QATestScenarioMapper {

    @Mapping(target = "requestId", source = "request.id")
    QATestScenarioResponse toResponse(QATestScenario scenario);
}
