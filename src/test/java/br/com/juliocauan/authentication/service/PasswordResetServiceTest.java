package br.com.juliocauan.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetException;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.PasswordResetServiceImpl;
import jakarta.persistence.EntityNotFoundException;

class PasswordResetServiceTest extends TestContext {

    private final PasswordResetServiceImpl passwordResetService;
    private final PasswordResetRepositoryImpl passwordResetRepository;
    private final PasswordEncoder encoder;

    public PasswordResetServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetServiceImpl passwordResetTokenService,
            PasswordResetRepositoryImpl passwordResetTokenRepository, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetService = passwordResetTokenService;
        this.passwordResetRepository = passwordResetTokenRepository;
        this.encoder = encoder;
    }

    @BeforeEach
    void standard() {
        passwordResetRepository.deleteAll();
        getUserRepository().deleteAll();
    }

    private final UserEntity saveUser() {
        return getUserRepository().save(UserEntity
                .builder()
                .username(getRandomUsername())
                .password(getRandomPassword())
                .build());
    }

    private final PasswordResetEntity savePasswordReset() {
        UserEntity user = saveUser();
        return passwordResetRepository.save(PasswordResetEntity
                .builder()
                .user(user)
                .token(getRandomToken())
                .build());
    }

    @Test
    void generateToken() {
        User user = saveUser();
        String token = passwordResetService.generateToken(user.getUsername());
        PasswordReset passwordResetToken = passwordResetRepository.findAll().get(0);

        assertEquals(user, passwordResetToken.getUser());
        assertEquals(token, passwordResetToken.getToken());
        assertFalse(passwordResetToken.isExpired());
    }

    @Test
    void generateToken_error_getByUsername() {
        String username = getRandomUsername();
        UsernameNotFoundException exception = assertThrowsExactly(
                UsernameNotFoundException.class,
                () -> passwordResetService.generateToken(username));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

    @Test
    void generateToken_branch_deletePreviousPasswordReset() {
        PasswordReset passwordResetBefore = savePasswordReset();
        String newToken = passwordResetService.generateToken(passwordResetBefore.getUser().getUsername());

        PasswordReset passwordResetAfter = passwordResetRepository.findAll().get(0);
        assertNotEquals(newToken, passwordResetBefore.getToken());
        assertEquals(newToken, passwordResetAfter.getToken());
        assertFalse(passwordResetRepository.getByToken(passwordResetBefore.getToken()).isPresent());
    }

    @Test
    void getEmailTemplate() {
        String token = getRandomToken();
        String expectedValue = "To reset your password, use the following token: %s %n%n This token will last 10 minutes"
                .formatted(token);
        String emailTemplate = passwordResetService.getEmailTemplate(token);
        assertEquals(expectedValue, emailTemplate);
    }

    @Test
    void resetPassword() {
        PasswordReset passwordReset = savePasswordReset();
        String newPassword = getRandomPassword();
        passwordResetService.resetPassword(newPassword, passwordReset.getToken());

        User user = getUserRepository().findAll().get(0);
        assertNotEquals(passwordReset.getUser().getPassword(), user.getPassword());
        assertTrue(encoder.matches(newPassword, user.getPassword()));
        assertTrue(passwordResetRepository.findAll().isEmpty());
    }

    @Test
    void resetPassword_error_getByToken() {
        String token = getRandomToken();
        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
                () -> passwordResetService.resetPassword(getRandomPassword(), token));
        assertEquals(getErrorPasswordResetNotFound(token), exception.getMessage());
    }

    @Test
    void resetPassword_error_isExpired() {
        PasswordResetEntity passwordReset = passwordResetRepository.save(PasswordResetEntity
                .builder()
                .user(saveUser())
                .token(getRandomToken())
                .expireDate(LocalDateTime.now().minusSeconds(1))
                .build());

        ExpiredPasswordResetException exception = assertThrowsExactly(ExpiredPasswordResetException.class,
                () -> passwordResetService.resetPassword(getRandomPassword(), passwordReset.getToken()));
        assertEquals("Expired Token!", exception.getMessage());
        assertTrue(passwordResetRepository.findAll().isEmpty());
    }

}
