package br.com.juliomariano.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliomariano.authentication.config.TestContext;
import br.com.juliomariano.authentication.domain.model.PasswordReset;
import br.com.juliomariano.authentication.domain.model.User;
import br.com.juliomariano.authentication.infrastructure.exception.ExpiredResetTokenException;
import br.com.juliomariano.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliomariano.authentication.infrastructure.repository.RoleRepository;
import br.com.juliomariano.authentication.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

class PasswordResetServiceTest extends TestContext {

    private final PasswordResetRepository passwordResetRepository;
    private final PasswordResetService passwordResetService;

    public PasswordResetServiceTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetRepository passwordResetRepository,
            PasswordResetService passwordResetService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetRepository = passwordResetRepository;
        this.passwordResetService = passwordResetService;
    }

    @BeforeEach
    void beforeEach() {
        passwordResetRepository.deleteAll();
        getUserRepository().deleteAll();
    }

    private final User saveUser() {
        return getUserRepository().save(new User(getRandomUsername(), getRandomPassword()));
    }

    private final PasswordReset savePasswordReset() {
        return passwordResetRepository.save(new PasswordReset(saveUser()));
    }

    @Test
    void findByToken() {
        PasswordReset passwordReset = savePasswordReset();
        String token = passwordReset.getToken();
        assertEquals(passwordReset, passwordResetService.findByToken(token));
    }

    @Test
    void findByToken_error_notPresent() {
        String token = getRandomToken();
        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
                () -> passwordResetService.findByToken(token));
        assertEquals(exception.getMessage(), "Token [%s] not found!".formatted(token));
    }

    @Test
    void findByToken_error_nullToken() {
        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
                () -> passwordResetService.findByToken(null));
        assertEquals(exception.getMessage(), "Token [null] not found!");
    }

    @Test
    void findByToken_error_expiredToken() {
        PasswordReset passwordReset = new PasswordReset(saveUser());
        passwordReset.setExpireDate(LocalDateTime.now().minusSeconds(1));
        passwordResetRepository.save(passwordReset);
        ExpiredResetTokenException exception = assertThrowsExactly(ExpiredResetTokenException.class,
                () -> passwordResetService.findByToken(passwordReset.getToken()));
        assertEquals("Expired Token!", exception.getMessage());
    }

    @Test
    void register() {
        User user = saveUser();
        PasswordReset passwordReset = passwordResetService.register(user);
        assertEquals(user, passwordReset.getUser());
        assertFalse(passwordReset.isExpired());
    }

    @Test
    void register_branch_tokenAlreadyExists() {
        User user = saveUser();
        String tokenBefore = passwordResetService.register(user).getToken();
        passwordResetService.register(user);
        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
                () -> passwordResetService.findByToken(tokenBefore));
        assertEquals("Token [%s] not found!".formatted(tokenBefore), exception.getMessage());
    }
}
