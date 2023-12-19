package br.com.juliocauan.authentication.repository;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class PasswordResetRepositoryTest extends TestContext {

    private final PasswordResetRepositoryImpl passwordResetTokenRepository;

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();
    private final String tokenPresent = getRandomToken();
    private final String tokenNotPresent = getRandomToken();

    private UserEntity userEntity;
    private PasswordResetEntity expectedEntity;

    public PasswordResetRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetRepositoryImpl passwordResetTokenRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override @BeforeAll
    public void setup(){
        super.setup();
        userEntity = getUserRepository().save(UserEntity.builder()
            .id(null)
            .username(username)
            .password(password)
            .roles(null)
        .build());
    }

    @BeforeEach
    void standard(){
        passwordResetTokenRepository.deleteAll();
        expectedEntity = passwordResetTokenRepository.save(PasswordResetEntity.builder()
            .id(null)
            .token(tokenPresent)
            .user(userEntity)
        .build());
    }

    @Test
    void getByToken() {
        assertEquals(expectedEntity, passwordResetTokenRepository.getByToken(tokenPresent).get());
    }

    @Test
    void getByToken_notPresent() {
        assertFalse(passwordResetTokenRepository.getByToken(tokenNotPresent).isPresent());
    }

    @Test
    void getByUser() {
        assertEquals(expectedEntity, passwordResetTokenRepository.getByUser(userEntity).get());
    }

    @Test
    void getByUser_notPresent() {
        assertFalse(passwordResetTokenRepository.getByUser(null).isPresent());
    }
    
}
