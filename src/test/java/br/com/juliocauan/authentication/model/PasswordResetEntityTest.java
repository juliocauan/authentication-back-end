package br.com.juliocauan.authentication.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import jakarta.validation.ConstraintViolationException;

class PasswordResetEntityTest extends TestContext {

    private final PasswordResetRepositoryImpl passwordResetRepository;

    private User user;

    public PasswordResetEntityTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetRepositoryImpl passwordResetRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetRepository = passwordResetRepository;
    }

    @BeforeEach
    void beforeEach() {
        getUserRepository().deleteAll();
        passwordResetRepository.deleteAll();
        user = saveUser(getRandomUsername(), getRandomPassword());
    }

    private final User saveUser(String username, String password) {
        return getUserRepository().save(new User(username, password));
    }

    private final PasswordResetEntity savePasswordReset(User user, String token) {
        return passwordResetRepository.save(PasswordResetEntity
                .builder()
                .token(token)
                .user(user)
                .build());
    }

    @Test
    void save() {
        String token = getRandomToken();
        assertDoesNotThrow(() -> savePasswordReset(user, token));
    }

    @Test
    void token_notBlank() {
        String tokenNull = null;
        assertThrows(ConstraintViolationException.class, () -> savePasswordReset(user, tokenNull));

        String tokenBlank = "                                           ";
        assertThrows(ConstraintViolationException.class, () -> savePasswordReset(user, tokenBlank));
    }

    @Test
    void token_size_min() {
        String tokenMin = getRandomString(42);

        assertThrows(ConstraintViolationException.class, () -> savePasswordReset(user, tokenMin));
        assertDoesNotThrow(() -> savePasswordReset(user, tokenMin + "A"));
    }

    @Test
    void token_size_max() {
        String tokenMax = getRandomString(44);

        assertThrows(ConstraintViolationException.class, () -> savePasswordReset(user, tokenMax));
        assertDoesNotThrow(() -> savePasswordReset(user, tokenMax.substring(1)));
    }

    @Test
    void user_notNull() {
        String token = getRandomToken();
        assertThrows(DataIntegrityViolationException.class, () -> savePasswordReset(null, token));
    }

    @Test
    void user_unique() {
        String token = getRandomToken();
        savePasswordReset(user, token);

        assertThrows(DataIntegrityViolationException.class, () -> savePasswordReset(user, token));
    }

    @Test
    void onDelete_cascadeDoesNotDeleteUser() {
        savePasswordReset(user, getRandomToken());

        passwordResetRepository.deleteAll();
        assertEquals(user, getUserRepository().findById(user.getId()).get());
    }

    @Test
    void onUserDelete_deletePasswordReset() {
        Integer passwordResetId = savePasswordReset(user, getRandomToken()).getId();

        assertTrue(passwordResetRepository.findById(passwordResetId).isPresent());
        getUserRepository().deleteById(user.getId());
        assertFalse(passwordResetRepository.findById(passwordResetId).isPresent());
    }

    @Test
    void isExpired() {
        PasswordResetEntity passwordReset = savePasswordReset(user, getRandomToken());

        assertFalse(passwordReset.isExpired());
        passwordReset.setExpireDate(LocalDateTime.now().minusSeconds(1));
        assertTrue(passwordReset.isExpired());
    }

}
