package br.com.juliocauan.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetException;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;

class PasswordResetServiceTest extends TestContext {

    private final PasswordResetService passwordResetService;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder encoder;

    public PasswordResetServiceTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetService passwordResetService,
            PasswordResetRepository passwordResetRepository, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetService = passwordResetService;
        this.passwordResetRepository = passwordResetRepository;
        this.encoder = encoder;
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
    void sendNewToken() {
        User user = saveUser();
        passwordResetService.sendNewToken(user.getUsername());
        PasswordReset passwordReset = passwordResetRepository.findAll().get(0);

        assertEquals(user, passwordReset.getUser());
        assertEquals(43, passwordReset.getToken().length());
        assertFalse(passwordReset.isExpired());
    }

    @Test
    void sendNewToken_error_getByUsername() {
        saveUser();
        String username = getRandomUsername();
        UsernameNotFoundException exception = assertThrowsExactly(
                UsernameNotFoundException.class,
                () -> passwordResetService.sendNewToken(username));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

    @Test
    void sendNewToken_branch_deletePreviousPasswordReset() {
        PasswordReset passwordResetBefore = savePasswordReset();
        passwordResetService.sendNewToken(passwordResetBefore.getUser().getUsername());

        PasswordReset passwordResetAfter = passwordResetRepository.findAll().get(0);
        assertNotEquals(passwordResetAfter.getToken(), passwordResetBefore.getToken());
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
    void resetPassword_error_findByToken() {
        savePasswordReset();
        String token = getRandomToken();
        JpaObjectRetrievalFailureException exception = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
                () -> passwordResetService.resetPassword(getRandomPassword(), token));
        assertEquals(getErrorPasswordResetNotFound(token), exception.getMessage());
    }

    @Test
    void resetPassword_error_isExpired() {
        PasswordReset passwordReset = new PasswordReset(saveUser());
        passwordReset.setExpireDate(LocalDateTime.now().minusSeconds(1));
        passwordResetRepository.save(passwordReset);

        ExpiredPasswordResetException exception = assertThrowsExactly(ExpiredPasswordResetException.class,
                () -> passwordResetService.resetPassword(getRandomPassword(), passwordReset.getToken()));
        assertEquals("Expired Token!", exception.getMessage());
        assertTrue(passwordResetRepository.findAll().isEmpty());
    }

}
