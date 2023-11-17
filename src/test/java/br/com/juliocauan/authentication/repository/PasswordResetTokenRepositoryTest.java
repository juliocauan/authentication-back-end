package br.com.juliocauan.authentication.repository;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class PasswordResetTokenRepositoryTest extends TestContext {

    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();
    private final String tokenPresent = getRandomToken();
    private final String tokenNotPresent = getRandomToken();

    private UserEntity userEntity;
    private PasswordResetTokenEntity expectedEntity;

    public PasswordResetTokenRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetTokenRepositoryImpl passwordResetTokenRepository) {
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
        expectedEntity = passwordResetTokenRepository.save(PasswordResetTokenEntity.builder()
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
