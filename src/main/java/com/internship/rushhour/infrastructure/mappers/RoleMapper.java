package com.internship.rushhour.infrastructure.mappers;

import com.internship.rushhour.domain.role.entity.Role;
import com.internship.rushhour.domain.role.models.RoleCreateDTO;
import com.internship.rushhour.domain.role.models.RoleGetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(source="roleName", target="name")
    Role roleDTOToEntity(RoleCreateDTO dto);

    @Mapping(source="name", target="roleName")
    RoleCreateDTO roleEntityToDTO(Role role);

    @Mapping(source="roleName", target="name")
    Role getRoleDTOToEntity(RoleGetDTO dto);

    @Mapping(source="name", target="roleName")
    RoleGetDTO roleEntityToGetDTO(Role role);
}
