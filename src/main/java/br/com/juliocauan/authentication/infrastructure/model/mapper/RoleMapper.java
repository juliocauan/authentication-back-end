package br.com.juliocauan.authentication.infrastructure.model.mapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.EnumRole;
import org.springframework.security.core.GrantedAuthority;

import br.com.juliocauan.authentication.domain.model.Role;

public interface RoleMapper {

    static Set<EnumRole> authoritiesToEnumRole(Collection<? extends GrantedAuthority> model) {
        return model.stream()
            .map(item -> EnumRole.fromValue(item.getAuthority()))
            .collect(Collectors.toSet());
    }

    static Set<EnumRole> setRoleToSetEnumRole(Set<? extends Role> model) {
        return model.stream().map(Role::getName).collect(Collectors.toSet());
    }

}
