package br.com.juliocauan.authentication.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.UserData;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredResetTokenException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordException;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;

class AuthenticationServiceTest extends TestContext {

    private final AuthenticationService authenticationService;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder encoder;
    private final String adminKey = "@Admin123";

    public AuthenticationServiceTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationService authenticationService,
            PasswordEncoder encoder, PasswordResetRepository passwordResetRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.passwordResetRepository = passwordResetRepository;
        this.encoder = encoder;
    }

    @BeforeEach
    void beforeEach() {
        getUserRepository().deleteAll();
    }

    private final User getUser() {
        return new User(getRandomUsername(), getRandomPassword());
    }

    private final User saveUser() {
        return getUserRepository().save(getUser());
    }

    private final PasswordReset savePasswordReset() {
        return passwordResetRepository.save(new PasswordReset(saveUser()));
    }

    @Test
    void authenticate() {
        User user = getUser();
        String rawPassword = user.getPassword();
        user.setPassword(encoder.encode(rawPassword));
        getUserRepository().save(user);
        UserData userData = authenticationService.authenticate(user.getUsername(), rawPassword);
        assertTrue(userData.getJWT().contains("."));
        assertTrue(userData.getRoles().isEmpty());
    }

    @Test
    void authenticate_error_badCredentials() {
        BadCredentialsException exception = assertThrowsExactly(BadCredentialsException.class,
                () -> authenticationService.authenticate(getRandomPassword(), getRandomPassword()));
        assertEquals("Bad credentials", exception.getMessage());
    }

    @Test
    void authenticate_error_disabled() {
        User user = getUser();
        user.setDisabled(true);
        getUserRepository().save(user);
        DisabledException exception = assertThrowsExactly(DisabledException.class,
                () -> authenticationService.authenticate(user.getUsername(), user.getPassword()));
        assertEquals("User is disabled", exception.getMessage());
    }

    @Test
    void authenticate_error_locked() {
        User user = getUser();
        user.setLocked(true);
        getUserRepository().save(user);
        LockedException exception = assertThrowsExactly(LockedException.class,
                () -> authenticationService.authenticate(user.getUsername(), user.getPassword()));
        assertEquals("User account is locked", exception.getMessage());
    }

    @Test
    void registerUser() {
        User expectedUser = getUser();
        authenticationService.registerUser(expectedUser.getUsername(), expectedUser.getPassword());

        User user = getUserRepository().findAll().get(0);
        assertEquals(expectedUser.getUsername(), user.getUsername());
        assertTrue(encoder.matches(expectedUser.getPassword(), user.getPassword()));
    }

    @Test
    void registerUser_error_entityExists() {
        User user = saveUser();

        DataIntegrityViolationException exception = assertThrowsExactly(DataIntegrityViolationException.class,
                () -> authenticationService.registerUser(user.getUsername(), user.getPassword()));
        assertEquals("Username [%s] is already taken!".formatted(user.getUsername()), exception.getMessage());
    }

    @Test
    void registerUser_error_passwordSecurity() {
        String password = "1234567itsq";

        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> authenticationService.registerUser(getRandomUsername(), password));
        assertEquals("Password is not strong!", exception.getMessage());
    }

    @Test
    void registerAdmin() {
        String username = getRandomUsername();
        String password = getRandomPassword();
        authenticationService.registerAdmin(username, password, adminKey);

        User user = getUserRepository().findAll().get(0);
        assertEquals(username, user.getUsername());
        assertTrue(encoder.matches(password, user.getPassword()));
        assertEquals("ADMIN", user.getRoles().stream().findFirst().get().getName());
    }

    @Test
    void registerAdmin_error_adminKey() {
        String adminKey = "1234567itsq";
        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> authenticationService.registerAdmin(getRandomUsername(), getRandomPassword(), adminKey));
        assertEquals("Admin Key is incorrect!", exception.getMessage());
    }

    @Test
    void registerAdmin_error_entityExists() {
        User user = saveUser();
        DataIntegrityViolationException exception = assertThrowsExactly(DataIntegrityViolationException.class,
                () -> authenticationService.registerAdmin(user.getUsername(), user.getPassword(), adminKey));
        assertEquals("Username [%s] is already taken!".formatted(user.getUsername()), exception.getMessage());
    }

    @Test
    void registerAdmin_error_passwordSecurity() {
        String password = "1234567itsq";

        PasswordException exception = assertThrowsExactly(PasswordException.class,
                () -> authenticationService.registerAdmin(getRandomUsername(), password, adminKey));
        assertEquals("Password is not strong!", exception.getMessage());
    }


    @Test
    void sendToken() {
        User user = saveUser();
        authenticationService.sendToken(user.getUsername());
        PasswordReset passwordReset = passwordResetRepository.findAll().get(0);

        assertEquals(user, passwordReset.getUser());
        assertEquals(43, passwordReset.getToken().length());
        assertFalse(passwordReset.isExpired());
    }

    @Test
    void sendToken_error_getByUsername() {
        saveUser();
        String username = getRandomUsername();
        UsernameNotFoundException exception = assertThrowsExactly(
                UsernameNotFoundException.class,
                () -> authenticationService.sendToken(username));
        assertEquals("Username [%s] not found!".formatted(username), exception.getMessage());
    }

    @Test
    void sendToken_branch_deletePreviousPasswordReset() {
        PasswordReset passwordResetBefore = savePasswordReset();
        authenticationService.sendToken(passwordResetBefore.getUser().getUsername());

        PasswordReset passwordResetAfter = passwordResetRepository.findAll().get(0);
        assertNotEquals(passwordResetAfter.getToken(), passwordResetBefore.getToken());
    }

    @Test
    void resetPassword() {
        PasswordReset passwordReset = savePasswordReset();
        String newPassword = getRandomPassword();
        authenticationService.resetPassword(newPassword, passwordReset.getToken());

        User user = getUserRepository().findAll().get(0);
        assertNotEquals(passwordReset.getUser().getPassword(), user.getPassword());
        assertTrue(encoder.matches(newPassword, user.getPassword()));
        assertTrue(passwordResetRepository.findAll().isEmpty());
    }

    @Test
    void resetPassword_error_findByToken() {
        savePasswordReset();
        String token = getRandomToken();
        JpaObjectRetrievalFailureException exception = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
                () -> authenticationService.resetPassword(getRandomPassword(), token));
        assertEquals("Token [%s] not found!".formatted(token), exception.getMessage());
    }

    @Test
    void resetPassword_error_isExpired() {
        PasswordReset passwordReset = new PasswordReset(saveUser());
        passwordReset.setExpireDate(LocalDateTime.now().minusSeconds(1));
        passwordResetRepository.save(passwordReset);

        ExpiredResetTokenException exception = assertThrowsExactly(ExpiredResetTokenException.class,
                () -> authenticationService.resetPassword(getRandomPassword(), passwordReset.getToken()));
        assertEquals("Expired Token!", exception.getMessage());
        assertTrue(passwordResetRepository.findAll().isEmpty());
    }


}
