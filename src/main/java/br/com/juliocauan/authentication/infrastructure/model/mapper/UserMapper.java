package br.com.juliocauan.authentication.infrastructure.model.mapper;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openapitools.model.SignupForm;
import org.openapitools.model.UserInfo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;

public interface UserMapper {

    static UserEntity domainToEntity(User model) {
        return UserEntity.builder()
            .id(model.getId())
            .password(model.getPassword())
            .username(model.getUsername())
            .roles(RoleMapper.domainToEntity(model.getRoles()))
        .build();
    }

    static UserEntity signupFormToEntity(SignupForm signupForm, Set<RoleEntity> roles, PasswordEncoder encoder) {
        return UserEntity.builder()
            .id(null)
            .password(encoder.encode(signupForm.getPassword()))
            .username(signupForm.getUsername())
            .roles(roles)
        .build();
    }

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
            //TODO repassar para RoleMapper
            .roles(model.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }

    static User entityToDomain(UserEntity model){
        return new User() {
            @Override
            public UUID getId(){return model.getId();}
            @Override
            public String getUsername(){return model.getUsername();}
            @Override
            public String getPassword(){return model.getPassword();}
            @Override
            public Set<? extends Role> getRoles(){return model.getRoles();}
        };
    }
    
}
