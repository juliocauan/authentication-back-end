package br.com.juliocauan.authentication.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class PasswordResetTokenEntityTest extends TestContext {

    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();
    private final String token = getRandomToken();

    private UserEntity userEntity;
    private PasswordResetTokenEntity passwordResetToken;

    public PasswordResetTokenEntityTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetTokenRepositoryImpl passwordResetTokenRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        passwordResetTokenRepository.deleteAll();
        userEntity = getUserRepository().save(UserEntity.builder()
            .password(password)
            .username(username)
            .roles(new HashSet<RoleEntity>())
        .build());

        passwordResetToken = passwordResetTokenRepository.save(PasswordResetTokenEntity.builder()
            .token(token)
            .user(userEntity)
        .build());
    }

    @Test
    void onDelete_cascadeDoesNotDeleteUser() {
        UUID userId = passwordResetToken.getUser().getId();
        passwordResetTokenRepository.deleteAll();
        assertEquals(userEntity, getUserRepository().findById(userId).get());
    }

    @Test
    void onUserDelete_deletePasswordResetToken() {
        Long id = passwordResetToken.getId();
        assertTrue(passwordResetTokenRepository.findById(id).isPresent());
        getUserRepository().deleteById(userEntity.getId());
        assertFalse(passwordResetTokenRepository.findById(id).isPresent());
    }

    @Test
    void isExpired() {
        assertFalse(passwordResetToken.isExpired());
        passwordResetToken.setExpireDate(LocalDateTime.now().minusSeconds(1));
        assertTrue(passwordResetToken.isExpired());
    }
    
}
