package com.example.demo.mapper;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.respone.RoleRespone;
import com.example.demo.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  @Mapping(target = "permission", ignore = true)
  Role toRole(RoleRequest roleRequest);

  RoleRespone toRoleRespone(Role role);

  @Mapping(target = "permission", ignore = true)
  void updateRole(@MappingTarget Role role, RoleRequest request);
}
