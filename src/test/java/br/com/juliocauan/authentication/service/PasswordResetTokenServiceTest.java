package br.com.juliocauan.authentication.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.PasswordResetTokenServiceImpl;

class PasswordResetTokenServiceTest extends TestContext {

    private final PasswordResetTokenServiceImpl passwordResetTokenService;
    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;

    private final String invalidUsername = "notPresent@email.test";
    private final String password = "12345678";
    //TODO review this email
    private final String username = "jcam.test01@gmail.com";
    private final int tokenLength = 43;

    private final String usernameNotFoundException = "User Not Found with username: " + invalidUsername;

    private UserEntity user;
    private Set<RoleEntity> roles = new HashSet<>();

    public PasswordResetTokenServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetTokenServiceImpl recoveryTokenService,
            PasswordResetTokenRepositoryImpl passwordResetTokenRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetTokenService = recoveryTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        user = UserEntity.builder()
            .password(password)
            .username(username)
            .roles(roles)
        .build();
    }

    @Test
    void givenNotPresentUsername_WhenGenerateLinkAndSendEmail_ThenUsernameNotFoundException() {
        UsernameNotFoundException exception = Assertions.assertThrowsExactly(
            UsernameNotFoundException.class,
            () -> passwordResetTokenService.buildTokenAndSendEmail(invalidUsername));

        //TODO review tests that compares message errors
        Assertions.assertTrue(exception.getMessage().contentEquals(usernameNotFoundException));
    }

    @Test
    void givenUsername_WhenGenerateLinkAndSendEmail_ThenVoid() {
        getUserRepository().save(user);
        Assertions.assertDoesNotThrow(() -> passwordResetTokenService.buildTokenAndSendEmail(user.getUsername()));
    }

    @Test
    void givenUsernameWithNoToken_WhenGenerateLinkAndSendEmail_ThenCreateRecoveryToken() {
        getUserRepository().save(user);
        passwordResetTokenService.buildTokenAndSendEmail(user.getUsername());
        PasswordResetToken token = passwordResetTokenRepository.findByUser(user).get();
        Assertions.assertTrue(passwordResetTokenRepository.findByToken(token.getToken()).isPresent());
    }

    @Test
    void givenUsernameWithToken_WhenGenerateLinkAndSendEmail_ThenDeletePreviousToken() {
        getUserRepository().save(user);
        passwordResetTokenService.buildTokenAndSendEmail(user.getUsername());
        PasswordResetToken previousToken = passwordResetTokenRepository.findByUser(user).get();
        passwordResetTokenService.buildTokenAndSendEmail(user.getUsername());
        PasswordResetToken newToken = passwordResetTokenRepository.findByUser(user).get();
        Assertions.assertNotEquals(newToken, previousToken);
        Assertions.assertFalse(passwordResetTokenRepository.findByToken(previousToken.getToken()).isPresent());
    }

    @Test
    void givenUsername_WhenGenerateLinkAndSendEmail_ThenGenerateToken() {
        getUserRepository().save(user);
        passwordResetTokenService.buildTokenAndSendEmail(user.getUsername());
        PasswordResetToken token = passwordResetTokenRepository.findByUser(user).get();
        Assertions.assertEquals(tokenLength, token.getToken().length());
    }
    
}
