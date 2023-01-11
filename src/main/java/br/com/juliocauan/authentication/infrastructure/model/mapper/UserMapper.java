package br.com.juliocauan.authentication.infrastructure.model.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.SignupForm;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;

//TODO test this
public abstract class UserMapper {

    public static UserEntity domainToEntity(User model) {
        return UserEntity.builder()
            .id(model.getId())
            .email(model.getEmail())
            .password(model.getPassword())
            .username(model.getUsername())
            .roles(RoleMapper.domainToEntity(model.getRoles()))
        .build();
    }

    public static UserEntity formToEntity(SignupForm signupForm, Set<RoleEntity> roles, PasswordEncoder encoder) {
        return UserEntity.builder()
            .id(null)
            .email(signupForm.getEmail())
            .password(encoder.encode(signupForm.getPassword()))
            .username(signupForm.getUsername())
            .roles(roles)
        .build();
    }

    public static UserPrincipal domainToPrincipal(User user) {
        UserPrincipal userPrincipal = new UserPrincipal();
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().getValue())).collect(Collectors.toSet());
        userPrincipal.setUsername(user.getUsername());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setAuthorities(authorities);
        return userPrincipal;
    }
    
}
