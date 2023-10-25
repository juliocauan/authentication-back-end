package br.com.juliocauan.authentication.model;

import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RecoveryTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class PasswordResetTokenEntityTest extends TestContext {

    private final RecoveryTokenRepositoryImpl passwordResetTokenRepository;

    private final String password = "12345678";
    private final String username = "test@email.com";
    private final String token = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    private UserEntity userEntity;
    private PasswordResetTokenEntity passwordResetToken;

    public PasswordResetTokenEntityTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, RecoveryTokenRepositoryImpl passwordResetTokenRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @BeforeEach
    public void standard(){
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
    void deleteCascadeDoesNotDeleteUser() {
        passwordResetTokenRepository.deleteAll();
        Assertions.assertEquals(userEntity, getUserRepository().findById(passwordResetToken.getUser().getId()).get());
    }

    @Test
    void ifDeleteUser_ThenDeletePasswordResetToken() {
        Assertions.assertTrue(passwordResetTokenRepository.findById(passwordResetToken.getId()).isPresent());
        getUserRepository().deleteById(userEntity.getId());
        Assertions.assertFalse(passwordResetTokenRepository.findById(passwordResetToken.getId()).isPresent());
    }
    
}
