package br.com.juliomariano.authentication.util.mapper;

import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.openapitools.model.UserInfo;

import br.com.juliomariano.authentication.domain.model.Role;
import br.com.juliomariano.authentication.domain.model.User;

@Mapper(imports = { Role.class, Collectors.class })
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles",
            expression = "java(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))")
    UserInfo toUserInfo(User user);

}
