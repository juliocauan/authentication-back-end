package br.com.juliocauan.authentication.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.UserData;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;

class AuthenticationServiceTest extends TestContext {

    private final AuthenticationService authenticationService;
    private final PasswordEncoder encoder;
    private final String adminKey = "@Admin123";

    public AuthenticationServiceTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationService authenticationService,
            PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
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
        assertEquals(getErrorUsernameDuplicated(user.getUsername()), exception.getMessage());
    }

    @Test
    void registerUser_error_passwordSecurity() {
        String password = "1234567itsq";

        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
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
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
                () -> authenticationService.registerAdmin(getRandomUsername(), getRandomPassword(), adminKey));
        assertEquals("Admin Key is incorrect!", exception.getMessage());
    }

    @Test
    void registerAdmin_error_entityExists() {
        User user = saveUser();
        DataIntegrityViolationException exception = assertThrowsExactly(DataIntegrityViolationException.class,
                () -> authenticationService.registerAdmin(user.getUsername(), user.getPassword(), adminKey));
        assertEquals(getErrorUsernameDuplicated(user.getUsername()), exception.getMessage());
    }

    @Test
    void registerAdmin_error_passwordSecurity() {
        String password = "1234567itsq";

        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
                () -> authenticationService.registerAdmin(getRandomUsername(), password, adminKey));
        assertEquals("Password is not strong!", exception.getMessage());
    }

}
