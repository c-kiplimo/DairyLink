package com.collicode.api.dairylink.service.mapper;

import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.web.rest.dto.CooperativeDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring",uses = {})
public interface CooperativeMapper extends EntityMapper <CooperativeDTO, User> {
CooperativeDTO toDto(User Entity);
}
