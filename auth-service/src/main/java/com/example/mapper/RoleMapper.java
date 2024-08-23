package com.example.mapper;

import com.example.dto.RoleDto;
import com.example.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role roleDtoToRole(RoleDto roleDto);

    RoleDto roleToRoleDto(Role role);

    @Mapping(target = "id", ignore = true)
    RoleDto roleToRoleDtoWithoutId(Role role);
}
