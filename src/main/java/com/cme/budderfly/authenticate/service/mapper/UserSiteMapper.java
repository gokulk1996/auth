package com.cme.budderfly.authenticate.service.mapper;

import com.cme.budderfly.authenticate.domain.*;
import com.cme.budderfly.authenticate.service.dto.UserSiteDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UserSite and its DTO UserSiteDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserSiteMapper extends EntityMapper<UserSiteDTO, UserSite> {


    @Mapping(target = "jhiUsers", ignore = true)
    UserSite toEntity(UserSiteDTO userSiteDTO);

    default UserSite fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserSite userSite = new UserSite();
        userSite.setId(id);
        return userSite;
    }
}
