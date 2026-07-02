package com.deliveryworkbench.mapper;

import com.deliveryworkbench.dto.AppUserResponse;
import com.deliveryworkbench.entity.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUserResponse toResponse(AppUser user);
}
