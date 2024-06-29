package br.com.juliocauan.authentication.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredResetTokenException;

class PasswordResetRepositoryTest extends TestContext {

    private final PasswordResetRepository passwordResetRepository;

    public PasswordResetRepositoryTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetRepository passwordResetRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetRepository = passwordResetRepository;
    }

    @BeforeEach
    void beforeEach(){
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
        assertEquals(passwordReset, passwordResetRepository.findByToken(token));
    }

    @Test
    void findByToken_error_notPresent() {
        String token = getRandomToken();
        JpaObjectRetrievalFailureException exception = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
            () -> passwordResetRepository.findByToken(token));
        assertEquals(exception.getMessage(), "Token [%s] not found!".formatted(token));
    }

    @Test
    void findByToken_error_nullToken() {
        JpaObjectRetrievalFailureException exception = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
            () -> passwordResetRepository.findByToken(null));
        assertEquals(exception.getMessage(), "Token [null] not found!");
    }

    @Test
    void findByToken_error_expiredToken() {
        PasswordReset passwordReset = new PasswordReset(saveUser());
        passwordReset.setExpireDate(LocalDateTime.now().minusSeconds(1));
        passwordResetRepository.save(passwordReset);
        ExpiredResetTokenException exception = assertThrowsExactly(ExpiredResetTokenException.class,
            () -> passwordResetRepository.findByToken(passwordReset.getToken()));
        assertEquals("Expired Token!", exception.getMessage());
    }

    @Test
    void register() {
        User user = saveUser();
        PasswordReset passwordReset = passwordResetRepository.register(user);
        assertEquals(user, passwordReset.getUser());
        assertFalse(passwordReset.isExpired());
    }

    @Test
    void register_branch_tokenAlreadyExists() {
        User user = saveUser();
        String tokenBefore = passwordResetRepository.register(user).getToken();
        passwordResetRepository.register(user);
        JpaObjectRetrievalFailureException exception = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
            () -> passwordResetRepository.findByToken(tokenBefore));
        assertEquals("Token [%s] not found!".formatted(tokenBefore), exception.getMessage());
    }
    
}
