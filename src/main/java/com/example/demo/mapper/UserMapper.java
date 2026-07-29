package com.example.demo.mapper;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.respone.UserRespone;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles",ignore = true)
    User toUser(UserRequest request);
    UserRespone toUserResponse(User user);

    @Mapping(target = "roles",ignore = true)
    void updateUser(@MappingTarget User user, UserRequest request);
}