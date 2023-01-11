package br.com.juliocauan.authentication.infrastructure.model.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.EnumRole;
import org.openapitools.model.SignupForm;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;

public abstract class UserMapper {

    //TODO review this mappers

    public static UserEntity domainToEntity(User model) {
        return UserEntity.builder()
            .email(model.getEmail())
            .password(model.getPassword())
            .username(model.getUsername())
            // .roles(model.getRoles().stream().map(role -> RoleMapper.domainToEntity(role)).collect(Collectors.toSet()))
        .build();
    }

    public static UserEntity formToEntityWithEncodedPassword(SignupForm signupForm, PasswordEncoder encoder) {
        User user = new User() {
            @Override
            public String getUsername() {return signupForm.getUsername();}
            @Override
            public String getEmail() {return signupForm.getEmail();}
            @Override
            public String getPassword() {return encoder.encode(signupForm.getPassword());}
            @Override
            public Set<Role> getRoles() {
                return signupForm.getRoles().stream().map(role -> new Role(){
                    @Override
                    public EnumRole getName() {return role;}
                }).collect(Collectors.toSet());
            }
        };
        return domainToEntity(user);
    }

    public static UserPrincipal userToPrincipal(User user) {
        UserPrincipal userPrincipal = new UserPrincipal();
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().getValue())).collect(Collectors.toSet());
        userPrincipal.setUsername(user.getUsername());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setAuthorities(authorities);
        return userPrincipal;
    }
    
}
