package com.collicode.api.dairylink.service.mapper;

import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.web.rest.dto.UserDTO;
import org.mapstruct.factory.Mappers;

public interface UserMapper {
    UserMapper instance = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User user);
}

