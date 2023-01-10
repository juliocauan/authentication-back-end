package br.com.juliocauan.authentication.infrastructure.model.mapper;

import org.openapitools.model.SignupForm;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;

public abstract class UserMapper {

    public static UserEntity domainToEntity(User model) {
        return UserEntity.builder()
            .email(model.getEmail())
            .keyPassword(model.getKeyPassword())
            .accessName(model.getAccessName())
        .build();
    }

    public static UserEntity formToEntityWithEncodedPassword(SignupForm signupForm, PasswordEncoder encoder) {
        User user = new User() {
            @Override
            public String getAccessName() {return signupForm.getUsername();}
            @Override
            public String getEmail() {return signupForm.getEmail();}
            @Override
            public String getKeyPassword() {return encoder.encode(signupForm.getPassword());}
        };
        return domainToEntity(user);
    }
    
}
