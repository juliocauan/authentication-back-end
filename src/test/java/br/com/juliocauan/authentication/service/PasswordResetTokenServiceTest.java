package br.com.juliocauan.authentication.service;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetTokenException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordMatchException;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.PasswordResetTokenServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;

class PasswordResetTokenServiceTest extends TestContext {

    private final PasswordResetTokenServiceImpl passwordResetTokenService;
    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;
    private final UserServiceImpl userService;

    @Value("${test.mail.receiver}")
    private String username;
    private final String password = "12345678";
    private final String tokenMock = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    private final String usernameNotPresent = "notPresent@email.test";
    private final String usernameNotFoundException = "User Not Found with username: " + usernameNotPresent;
    private final String passwordConfirmationException = "Confirmation and new password are different!";
    private final String entityNotFoundException = "Password Reset Token not found with token: " + tokenMock;
    private final String expiredPasswordResetTokenException = "Expired Password Reset Token!";
    private final String errorMailSend = "The recipient address is not a valid address!";

    private final int tokenLength = 43;

    private UserEntity user;

    public PasswordResetTokenServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetTokenServiceImpl passwordResetTokenService,
            PasswordResetTokenRepositoryImpl passwordResetTokenRepository, UserServiceImpl userService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userService = userService;
    }

    @BeforeEach
    public void standard(){
        passwordResetTokenRepository.deleteAll();
        getUserRepository().deleteAll();
        user = getUserRepository().save(UserEntity
            .builder()
                .username(username)
                .password(password)
                .roles(new HashSet<>())
            .build());
    }

    @Test
    void generateToken() {
        String token = Assertions.assertDoesNotThrow(() -> passwordResetTokenService.generateToken(user.getUsername()));
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.getByToken(token).get();
        
        Assertions.assertEquals(user, passwordResetToken.getUser());
        Assertions.assertEquals(token, passwordResetToken.getToken());
        Assertions.assertEquals(tokenLength, passwordResetToken.getToken().length());
        Assertions.assertFalse(passwordResetToken.isExpired());
    }
    
    @Test
    void generateToken_branch_deletePreviousPasswordResetToken() {
        PasswordResetToken passwordResetTokenBefore = passwordResetTokenRepository.save(PasswordResetTokenEntity.builder()
            .token(tokenMock)
            .user(user)
        .build());
        String token = Assertions.assertDoesNotThrow(() -> passwordResetTokenService.generateToken(user.getUsername()));
        PasswordResetToken passwordResetTokenAfter = passwordResetTokenRepository.getByToken(token).get();
        
        Assertions.assertNotEquals(passwordResetTokenBefore, passwordResetTokenAfter);
        Assertions.assertFalse(passwordResetTokenRepository.getByToken(passwordResetTokenBefore.getToken()).isPresent());
    }

    @Test
    void generateToken_error_getByUsername() {
        UsernameNotFoundException exception = Assertions.assertThrowsExactly(
            UsernameNotFoundException.class,
            () -> passwordResetTokenService.generateToken(usernameNotPresent));
        Assertions.assertTrue(exception.getMessage().contentEquals(usernameNotFoundException));
    }

    @Test
    void sendEmail() {
        String expectedEmailBody = String.format("To reset your password, use the following token: %s %n%n This token will last %d minutes",
            tokenMock, PasswordResetToken.TOKEN_EXPIRATION_MINUTES);
        String emailBody = Assertions.assertDoesNotThrow(() -> passwordResetTokenService.sendEmail(username, tokenMock));
        Assertions.assertEquals(expectedEmailBody, emailBody);
    }

    @Test
    void sendEmail_error_mailSend() {
        MailSendException exception = Assertions.assertThrowsExactly(MailSendException.class, () -> 
            passwordResetTokenService.sendEmail("notAnEmail", tokenMock));
        Assertions.assertEquals(errorMailSend, exception.getMessage());
    }

    @Test
    void resetPassword() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password).passwordConfirmation(password);
        PasswordResetToken passwordResetTokenBeforeUpdate = passwordResetTokenRepository.save(PasswordResetTokenEntity
            .builder()
                .token(tokenMock)
                .user(user)
            .build());
        Assertions.assertDoesNotThrow(() -> passwordResetTokenService.resetPassword(passwordMatch, tokenMock));

        User userAfterUpdate = userService.getByUsername(username);
        Assertions.assertNotEquals(passwordResetTokenBeforeUpdate.getUser().getPassword(), userAfterUpdate.getPassword());
        Assertions.assertFalse(passwordResetTokenRepository.getByToken(tokenMock).isPresent());
    }

    @Test
    void resetPassword_error_checkPasswordConfirmation() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password).passwordConfirmation("differentPassword");
        PasswordMatchException exception = Assertions.assertThrowsExactly(PasswordMatchException.class,
            () -> passwordResetTokenService.resetPassword(passwordMatch, tokenMock));
        Assertions.assertEquals(passwordConfirmationException, exception.getMessage());
    }

    @Test
    void resetPassword_error_findByToken() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password).passwordConfirmation(password);
        EntityNotFoundException exception = Assertions.assertThrowsExactly(EntityNotFoundException.class,
            () -> passwordResetTokenService.resetPassword(passwordMatch, tokenMock));
        Assertions.assertEquals(entityNotFoundException, exception.getMessage());
    }

    @Test
    void resetPassword_error_isExpired() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password).passwordConfirmation(password);
        passwordResetTokenRepository.save(PasswordResetTokenEntity
            .builder()
                .expireDate(LocalDateTime.now().minusSeconds(1))
                .token(tokenMock)
                .user(user)
            .build());
        ExpiredPasswordResetTokenException exception = Assertions.assertThrowsExactly(ExpiredPasswordResetTokenException.class,
            () -> passwordResetTokenService.resetPassword(passwordMatch, tokenMock));
        Assertions.assertEquals(expiredPasswordResetTokenException, exception.getMessage());
    }
    
}
