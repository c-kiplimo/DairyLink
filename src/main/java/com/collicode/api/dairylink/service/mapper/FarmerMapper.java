package com.collicode.api.dairylink.service.mapper;

import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.web.rest.dto.FarmerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {})
public interface FarmerMapper extends EntityMapper<FarmerDTO, User>{
    FarmerDTO toDto(User Entity);
}
