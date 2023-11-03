package br.com.juliocauan.authentication.infrastructure.model.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;

public interface UserMapper {

    static UserPrincipal domainToUserPrincipal(User model) {
        UserPrincipal userPrincipal = new UserPrincipal();
        Set<SimpleGrantedAuthority> authorities = model.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().getValue())).collect(Collectors.toSet());
        userPrincipal.setUsername(model.getUsername());
        userPrincipal.setPassword(model.getPassword());
        userPrincipal.setAuthorities(authorities);
        return userPrincipal;
    }

    static UserInfo domainToUserInfo(User model){
        return new UserInfo()
            .id(model.getId())
            .username(model.getUsername())
            .roles(model.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }
    
}
