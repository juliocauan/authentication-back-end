package br.com.juliocauan.authentication.infrastructure.model.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;

//TODO test this
public abstract class RoleMapper {
    
    public static RoleEntity domainToEntity(Role model) {
        return RoleEntity.builder()
            .id(model.getId())
            .name(model.getName())
        .build();
    }

    public static Set<RoleEntity> domainToEntity(Set<? extends Role> model) {
        return model.stream().map(role -> domainToEntity(role)).collect(Collectors.toSet());
    }

}
