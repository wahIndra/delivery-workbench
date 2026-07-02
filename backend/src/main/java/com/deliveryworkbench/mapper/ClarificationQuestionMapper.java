package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.ClarificationQuestionResponse;
import com.deliveryworkbench.entity.ClarificationQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClarificationQuestionMapper {

    @Mapping(target = "requestId", source = "request.id")
    ClarificationQuestionResponse toResponse(ClarificationQuestion question);
}
