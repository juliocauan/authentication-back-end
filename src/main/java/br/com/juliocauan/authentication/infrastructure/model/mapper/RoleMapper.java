package br.com.juliocauan.authentication.infrastructure.model.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.EnumRole;
import org.springframework.security.core.GrantedAuthority;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;

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

    public static List<EnumRole> authoritiesToEnumRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
            .map(item -> EnumRole.fromValue(item.getAuthority()))
            .collect(Collectors.toList());
    }

}
