package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.AIAuditLogResponse;
import com.deliveryworkbench.entity.AIAuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AIAuditLogMapper {

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "requestCode", source = "request.requestCode")
    AIAuditLogResponse toResponse(AIAuditLog log);
}
