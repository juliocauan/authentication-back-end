package br.com.juliocauan.authentication.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class PasswordResetRepositoryTest extends TestContext {

    private final PasswordResetRepositoryImpl passwordResetRepository;

    public PasswordResetRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetRepositoryImpl passwordResetRepository) {
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

    private final PasswordResetEntity savePasswordReset() {
        User user = saveUser();
        return passwordResetRepository.save(PasswordResetEntity
                .builder()
                .user(user)
                .token(getRandomToken())
                .build());
    }

    @Test
    void getByToken() {
        PasswordReset passwordReset = savePasswordReset();
        String token = passwordReset.getToken();
        assertEquals(passwordReset, passwordResetRepository.getByToken(token).get());
    }

    @Test
    void getByToken_notPresent() {
        assertFalse(passwordResetRepository.getByToken(getRandomToken()).isPresent());
    }

    @Test
    void getByUser() {
        PasswordReset passwordReset = savePasswordReset();
        User user = passwordReset.getUser();
        assertEquals(passwordReset, passwordResetRepository.getByUser(user).get());
    }

    @Test
    void getByUser_notPresent() {
        User user = saveUser();
        assertFalse(passwordResetRepository.getByUser(user).isPresent());
    }

    @Test
    void register() {
        User user = saveUser();
        PasswordReset passwordReset = passwordResetRepository.register(user);
        assertEquals(user, passwordReset.getUser());
        assertFalse(passwordReset.isExpired());
    }

    @Test
    void delete() {
        PasswordReset passwordReset = savePasswordReset();
        assertFalse(passwordResetRepository.findAll().isEmpty());
        passwordResetRepository.delete(passwordReset);
        assertTrue(passwordResetRepository.findAll().isEmpty());
    }
    
}
