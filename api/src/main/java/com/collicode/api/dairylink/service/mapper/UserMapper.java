package com.collicode.api.dairylink.service.mapper;

import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.web.rest.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {
    UserMapper instance = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User user);
}

