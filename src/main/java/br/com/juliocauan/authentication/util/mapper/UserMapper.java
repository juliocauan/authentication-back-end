package br.com.juliocauan.authentication.util.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;
import lombok.experimental.UtilityClass;

//TODO refactor to mapstruct
@UtilityClass
public final class UserMapper {

    public static UserDetails domainToUserDetails(User model) {
        UserPrincipal userPrincipal = new UserPrincipal();
        Set<SimpleGrantedAuthority> authorities = model.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
        userPrincipal.setUsername(model.getUsername());
        userPrincipal.setPassword(model.getPassword());
        userPrincipal.setLocked(model.isLocked());
        userPrincipal.setDisabled(model.isDisabled());
        userPrincipal.setAuthorities(authorities);
        return userPrincipal;
    }

    public static UserInfo domainToUserInfo(User model){
        return new UserInfo()
            .id(model.getId())
            .username(model.getUsername())
            .roles(model.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }
    
}
