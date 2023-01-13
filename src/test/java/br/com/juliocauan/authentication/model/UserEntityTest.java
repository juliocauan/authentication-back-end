package br.com.juliocauan.authentication.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import jakarta.validation.ConstraintViolationException;

public class UserEntityTest extends TestContext {

    private final String usernameBlank = " ";
    private final String usernameMin = "aaaaa";
    private final String usernameMax = "aaaaaaaaaaaaaaaaaaaaa";
    private final String usernameMinValid = "aaaaaa";
    private final String usernameMaxValid = "aaaaaaaaaaaaaaaaaaaa";

    private final String emailValid = "test@email.com";
    private final String email = "testnotmail.com";
    private final String emailBlank = " ";
    private final String emailMax = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@email.com";
    private final String emailMaxValid = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@email.com";

    private final String passwordValid = "1234567890";
    private final String passwordBlank = " ";
    private final String passwordMin = "1234567";
    private final String passwordMax = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    private final String passwordMinValid = "12345678";
    private final String passwordMaxValid = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    private UserEntity entity;

    public UserEntityTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        entity = UserEntity.builder()
            .id(null)
            .email(emailValid)
            .password(passwordValid)
            .username(usernameMinValid)
            .roles(null)
        .build();
    }

    @Test
    public void usernameValidConstraints(){
        entity.setUsername(usernameMinValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
        getUserRepository().deleteAll();

        entity.setUsername(usernameMaxValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
    }

    @Test
    public void usernameBlankConstraint(){
        entity.setUsername(usernameBlank);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }

    @Test
    public void usernameMaxConstraint(){
        entity.setUsername(usernameMax);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }

    @Test
    public void usernameMinConstraint(){
        entity.setUsername(usernameMin);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }

    @Test
    public void emailValidConstraints(){
        entity.setEmail(emailValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
        getUserRepository().deleteAll();

        entity.setEmail(emailMaxValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
    }

    @Test
    public void emailBlankConstraint(){
        entity.setEmail(emailBlank);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }

    @Test
    public void emailConstraint(){
        entity.setEmail(email);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }

    @Test
    public void emailMaxConstraint(){
        entity.setEmail(emailMax);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }
    
    @Test
    public void passwordValidConstraints(){
        entity.setPassword(passwordValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
        getUserRepository().deleteAll();

        entity.setPassword(passwordMaxValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
        getUserRepository().deleteAll();

        entity.setPassword(passwordMinValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
    }

    @Test
    public void passwordBlankConstraint(){
        entity.setPassword(passwordBlank);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }

    @Test
    public void passwordMinConstraint(){
        entity.setPassword(passwordMin);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }

    @Test
    public void passwordMaxConstraint(){
        entity.setPassword(passwordMax);
        Assertions.assertThrows(ConstraintViolationException.class, () -> getUserRepository().save(entity));
    }

}
