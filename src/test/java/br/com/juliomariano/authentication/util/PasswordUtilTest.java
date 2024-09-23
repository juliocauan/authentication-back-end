package br.com.juliomariano.authentication.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliomariano.authentication.config.TestContext;
import br.com.juliomariano.authentication.infrastructure.exception.PasswordException;
import br.com.juliomariano.authentication.infrastructure.repository.RoleRepository;
import br.com.juliomariano.authentication.infrastructure.repository.UserRepository;

class PasswordUtilTest extends TestContext {

    private final PasswordEncoder encoder;

    private final String errorInvalidPassword = "Passwords don't match!";
    private final String errorWeakPassword = "Password is not strong!";

    public PasswordUtilTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.encoder = encoder;
    }

    @Test
    void encodePassword() {
        String rawPassword = getRandomPassword();
        String encodedPassword = PasswordUtil.encode(rawPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void validateMatch() {
        String rawPassword = getRandomPassword();
        String encodedPassword = PasswordUtil.encode(rawPassword);
        assertDoesNotThrow(() -> PasswordUtil.validateMatch(rawPassword, encodedPassword));
    }

    @Test
    void validateMatch_error_invalidPassword() {
        String rawPassword = getRandomPassword();
        String encodedPassword = PasswordUtil.encode(getRandomPassword());
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateMatch(rawPassword, encodedPassword));
        assertEquals(errorInvalidPassword, exception.getMessage());
    }

    @Test
    void validatePasswordMatch() {
        String password = getRandomPassword();
        PasswordMatch passwordMatch = new PasswordMatch();

        passwordMatch.password(password).passwordConfirmation(password);
        assertDoesNotThrow(() -> PasswordUtil.validateMatch(passwordMatch));
    }

    @Test
    void validatePasswordMatch_error_invalidPassword() {
        String password = getRandomPassword();
        String otherPassword = getRandomPassword();
        PasswordMatch passwordMatch = new PasswordMatch();

        passwordMatch.password(password).passwordConfirmation(otherPassword);
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateMatch(passwordMatch));
        assertEquals(errorInvalidPassword, exception.getMessage());

        passwordMatch.password(otherPassword).passwordConfirmation(password);
        exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateMatch(passwordMatch));
        assertEquals(errorInvalidPassword, exception.getMessage());
    }

    @Test
    void validateSecurity() {
        String strongPassword = getRandomPassword();
        assertDoesNotThrow(() -> PasswordUtil.validateSecurity(strongPassword));
    }

    @Test
    void validateSecurity_error_weakNumber() {
        String weakNumber = "@Password";
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateSecurity(weakNumber));
        assertEquals(errorWeakPassword, exception.getMessage());
    }

    @Test
    void validateSecurity_error_weakSpecial() {
        String weakSpecial = "APass123";
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateSecurity(weakSpecial));
        assertEquals(errorWeakPassword, exception.getMessage());
    }

    @Test
    void validateSecurity_error_weakLowerCase() {
        String weakLowerCase = "@PASS123";
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateSecurity(weakLowerCase));
        assertEquals(errorWeakPassword, exception.getMessage());
    }

    @Test
    void validateSecurity_error_weakUpperCase() {
        String weakUpperCase = "@pass123";
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateSecurity(weakUpperCase));
        assertEquals(errorWeakPassword, exception.getMessage());
    }

    @Test
    void validateSecurity_error_weakSize() {
        String weakSize = "@Pass12";
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateSecurity(weakSize));
        assertEquals(errorWeakPassword, exception.getMessage());
    }

    @Test
    void validateAdminKey() {
        String adminKey = "@Admin123";
        assertDoesNotThrow(() -> PasswordUtil.validateAdminKey(adminKey));
    }

    @Test
    void validateAdminKey_error_invalidPassword() {
        String adminKey = "@AnotherPassword123";
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> PasswordUtil.validateAdminKey(adminKey));
        assertEquals("Admin Key is incorrect!", exception.getMessage());
    }

}
