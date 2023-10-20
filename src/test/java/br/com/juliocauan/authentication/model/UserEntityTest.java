package br.com.juliocauan.authentication.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.TransactionSystemException;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class UserEntityTest extends TestContext {

    private final String usernameValid = "test@email.com";
    private final String usernameInvalid = "testnotmail.com";
    private final String usernameBlank = " ";
    private final String usernameMaxInvalid = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@email.com";
    private final String usernameMaxValid = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@email.com";

    private final String passwordValid = "1234567890";
    private final String passwordBlank = " ";
    private final String passwordMinInvalid = "1234567";
    private final String passwordMinValid = "12345678";
    private final String passwordMaxInvalid = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    private final String passwordMaxValid = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    
    private final UserRepositoryImpl repository = getUserRepository();

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
            .username(usernameValid)
            .password(passwordValid)
            .roles(null)
        .build();
    }

    @Test
    void usernameValidConstraints(){
        entity.setUsername(usernameValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
        getUserRepository().deleteAll();
        
        entity.setUsername(usernameMaxValid);
        Assertions.assertDoesNotThrow(() -> getUserRepository().save(entity));
    }

    @Test
    void usernameBlankConstraint(){
        entity.setUsername(usernameBlank);
        Assertions.assertThrows(TransactionSystemException.class, () -> repository.save(entity));
    }

    @Test
    void usernameEmailConstraint(){
        entity.setUsername(usernameInvalid);
        Assertions.assertThrows(TransactionSystemException.class, () -> repository.save(entity));
    }

    @Test
    void usernameMaxConstraint(){
        entity.setUsername(usernameMaxInvalid);
        Assertions.assertThrows(TransactionSystemException.class, () -> repository.save(entity));
    }
    
    @Test
    void passwordValidConstraints(){
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
    void passwordBlankConstraint(){
        entity.setPassword(passwordBlank);
        Assertions.assertThrows(TransactionSystemException.class, () -> repository.save(entity));
    }

    @Test
    void passwordMinConstraint(){
        entity.setPassword(passwordMinInvalid);
        Assertions.assertThrows(TransactionSystemException.class, () -> repository.save(entity));
    }

    @Test
    void passwordMaxConstraint(){
        entity.setPassword(passwordMaxInvalid);
        Assertions.assertThrows(TransactionSystemException.class, () -> repository.save(entity));
    }

}
