package br.com.juliocauan.authentication.service.util;

import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.service.util.PasswordService;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class PasswordServiceTest extends TestContext {

    private final PasswordService passwordService;
    private final PasswordEncoder encoder;

    private final String password1 = getRandomPassword();
    private final String password2 = getRandomPassword();

    private final String invalidPasswordError = "Passwords don't match!";

    public PasswordServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordService passwordService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordService = passwordService;
        this.encoder = encoder;
    }

    @Test
    void checkPasswordConfirmation() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password1).passwordConfirmation(password1);
        assertDoesNotThrow(() -> passwordService.validatePasswordMatch(passwordMatch));
        
        passwordMatch.password(password1).passwordConfirmation(password2);
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> passwordService.validatePasswordMatch(passwordMatch));
        assertEquals(invalidPasswordError, exception.getMessage());

        passwordMatch.password(password2).passwordConfirmation(password1);
        exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> passwordService.validatePasswordMatch(passwordMatch));
        assertEquals(invalidPasswordError, exception.getMessage());
    }

    @Test
    void checkPasswordConfirmation_error_passwordMatch() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password1).passwordConfirmation(password2);
        
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> passwordService.validatePasswordMatch(passwordMatch));
        assertEquals(invalidPasswordError, exception.getMessage());

        passwordMatch.password(password2).passwordConfirmation(password1);
        exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> passwordService.validatePasswordMatch(passwordMatch));
        assertEquals(invalidPasswordError, exception.getMessage());
    }

    @Test
    void encodePassword() {
        String encodedPassword = passwordService.encode(password1);
        assertNotEquals(password1, encodedPassword);
        assertTrue(encoder.matches(password1, encodedPassword));
    }

    @Test
    void checkCurrentPassword() {
        String encodedPassword = passwordService.encode(password1);
        assertDoesNotThrow(() -> passwordService.validatePasswordMatch(password1, encodedPassword));
    }

    @Test
    void checkCurrentPassword_error_invalidPassword() {
        String encodedPassword = passwordService.encode(password1);
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> passwordService.validatePasswordMatch(encodedPassword, password2));
        assertEquals(invalidPasswordError, exception.getMessage());
    }
    
}
