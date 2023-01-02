package br.com.juliocauan.authentication.infrastructure.model.mapper;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;

public abstract class RoleMapper {
    
    public static RoleEntity domainToEntity(Role model) {
        return RoleEntity.builder()
            .name(model.getName())
        .build();
    }

}
