package br.com.juliocauan.authentication.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetException;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.PasswordResetServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;

class PasswordResetServiceTest extends TestContext {

    private final PasswordResetServiceImpl passwordResetTokenService;
    private final PasswordResetRepositoryImpl passwordResetTokenRepository;
    private final UserServiceImpl userService;

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();
    private final String tokenMock = getRandomToken();

    private final String usernameNotPresent = "notPresent@email.test";
    private final String invalidPasswordException = "Passwords don't match!";
    private final String expiredPasswordResetException = "Expired Token!";

    private UserEntity user;

    public PasswordResetServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetServiceImpl passwordResetTokenService,
            PasswordResetRepositoryImpl passwordResetTokenRepository, UserServiceImpl userService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userService = userService;
    }

    @BeforeEach
    void standard(){
        passwordResetTokenRepository.deleteAll();
        getUserRepository().deleteAll();
        user = getUserRepository().save(UserEntity
            .builder()
                .username(username)
                .password(password)
                .roles(null)
            .build());
    }

    @Test
    void generateToken() {
        String token = assertDoesNotThrow(() -> passwordResetTokenService.generateToken(user.getUsername()));
        PasswordReset passwordResetToken = passwordResetTokenRepository.getByToken(token).get();
        
        assertEquals(user, passwordResetToken.getUser());
        assertEquals(token, passwordResetToken.getToken());
        assertEquals(43, passwordResetToken.getToken().length());
        assertFalse(passwordResetToken.isExpired());
    }
    
    @Test
    void generateToken_branch_deletePreviousPasswordReset() {
        PasswordReset passwordResetTokenBefore = passwordResetTokenRepository.save(PasswordResetEntity.builder()
            .token(tokenMock)
            .user(user)
        .build());
        String token = assertDoesNotThrow(() -> passwordResetTokenService.generateToken(user.getUsername()));
        PasswordReset passwordResetTokenAfter = passwordResetTokenRepository.getByToken(token).get();
        
        assertNotEquals(passwordResetTokenBefore, passwordResetTokenAfter);
        assertFalse(passwordResetTokenRepository.getByToken(passwordResetTokenBefore.getToken()).isPresent());
    }

    @Test
    void generateToken_error_getByUsername() {
        UsernameNotFoundException exception = assertThrowsExactly(
            UsernameNotFoundException.class,
            () -> passwordResetTokenService.generateToken(usernameNotPresent));
        assertTrue(exception.getMessage().contentEquals(getErrorUsernameNotFound(usernameNotPresent)));
    }

    @Test
    void resetPassword() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password).passwordConfirmation(password);
        PasswordReset passwordResetTokenBeforeUpdate = passwordResetTokenRepository.save(PasswordResetEntity
            .builder()
                .token(tokenMock)
                .user(user)
            .build());
        assertDoesNotThrow(() -> passwordResetTokenService.resetPassword(passwordMatch, tokenMock));

        User userAfterUpdate = userService.getByUsername(username);
        assertNotEquals(passwordResetTokenBeforeUpdate.getUser().getPassword(), userAfterUpdate.getPassword());
        assertFalse(passwordResetTokenRepository.getByToken(tokenMock).isPresent());
    }

    @Test
    void resetPassword_error_checkPasswordConfirmation() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password).passwordConfirmation("differentPassword");
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> passwordResetTokenService.resetPassword(passwordMatch, tokenMock));
        assertEquals(invalidPasswordException, exception.getMessage());
    }

    @Test
    void resetPassword_error_getByToken() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password).passwordConfirmation(password);
        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
            () -> passwordResetTokenService.resetPassword(passwordMatch, tokenMock));
        assertEquals(getErrorPasswordResetNotFound(tokenMock), exception.getMessage());
    }

    @Test
    void resetPassword_error_isExpired() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password).passwordConfirmation(password);
        Integer id = passwordResetTokenRepository.save(PasswordResetEntity
            .builder()
                .expireDate(LocalDateTime.now().minusSeconds(1))
                .token(tokenMock)
                .user(user)
            .build()).getId();
        ExpiredPasswordResetException exception = assertThrowsExactly(ExpiredPasswordResetException.class,
            () -> passwordResetTokenService.resetPassword(passwordMatch, tokenMock));
        assertEquals(expiredPasswordResetException, exception.getMessage());
        assertFalse(passwordResetTokenRepository.findById(id).isPresent());
    }
    
}
