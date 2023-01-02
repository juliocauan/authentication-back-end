package br.com.juliocauan.authentication.infrastructure.model.mapper;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;

public abstract class UserMapper {

    public static UserEntity domainToEntity(User model) {
        return UserEntity.builder()
            .email(model.getEmail())
            .password(model.getPassword())
            .username(model.getUsername())
        .build();
    }
    
}
