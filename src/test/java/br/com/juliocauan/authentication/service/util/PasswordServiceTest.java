package br.com.juliocauan.authentication.service.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordMatchException;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.PasswordService;

class PasswordServiceTest extends TestContext {

    private final PasswordService passwordService;
    private final PasswordEncoder encoder;

    private final String password1 = "1234567890";
    private final String password2 = "1234567890123";

    private final String currentPasswordError = "Wrong current password!";
    private final String confirmationPasswordError = "Confirmation and new password are different!";

    public PasswordServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordService passwordService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordService = passwordService;
        this.encoder = encoder;
    }

    @Test
    void checkPasswordConfirmation() {
        PasswordMatch passwordMatch = new PasswordMatch().password(password1).passwordConfirmation(password1);
        Assertions.assertDoesNotThrow(() -> passwordService.checkPasswordConfirmation(passwordMatch));
        
        passwordMatch.password(password1).passwordConfirmation(password2);
        PasswordMatchException exception = Assertions.assertThrowsExactly(PasswordMatchException.class,
            () -> passwordService.checkPasswordConfirmation(passwordMatch));
        Assertions.assertEquals(confirmationPasswordError, exception.getMessage());

        passwordMatch.password(password2).passwordConfirmation(password1);
        exception = Assertions.assertThrowsExactly(PasswordMatchException.class,
            () -> passwordService.checkPasswordConfirmation(passwordMatch));
        Assertions.assertEquals(confirmationPasswordError, exception.getMessage());
    }

    @Test
    void encodePassword() {
        String encodedPassword = passwordService.encode(password1);
        Assertions.assertNotEquals(password1, encodedPassword);
        Assertions.assertTrue(encoder.matches(password1, encodedPassword));
    }

    @Test
    void checkCurrentPassword() {
        String encodedPassword = passwordService.encode(password1);
        Assertions.assertDoesNotThrow(() -> passwordService.checkCurrentPassword(encodedPassword, password1));
        InvalidPasswordException exception = Assertions.assertThrowsExactly(InvalidPasswordException.class,
            () -> passwordService.checkCurrentPassword(encodedPassword, password2));
        Assertions.assertEquals(currentPasswordError, exception.getMessage());
    }
    
}
