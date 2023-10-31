package br.com.juliocauan.authentication.repository;

import java.util.HashSet;

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

    private final String username = "test@email.com";
    private final String password = "1234567890";
    private final String tokenPresent = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private final String tokenNotPresent = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";

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
            .roles(new HashSet<>())
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
    void givenPresentToken_WhenFindByToken_ThenPasswordResetToken() {
        assertEquals(expectedEntity, passwordResetTokenRepository.getByToken(tokenPresent).get());
    }

    @Test
    void givenNotPresentToken_WhenFindByToken_ThenPasswordResetTokenNotPresent() {
        assertFalse(passwordResetTokenRepository.getByToken(tokenNotPresent).isPresent());
    }

    @Test
    void givenPresentUser_WhenFindByUser_ThenPasswordResetToken() {
        assertEquals(expectedEntity, passwordResetTokenRepository.getByUser(userEntity).get());
    }

    @Test
    void givenNotPresentUser_WhenFindByUser_ThenPasswordResetTokenNotPresent() {
        assertFalse(passwordResetTokenRepository.getByUser(null).isPresent());
    }
    
}
