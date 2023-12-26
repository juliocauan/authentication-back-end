package br.com.juliocauan.authentication.service.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.UserData;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;
import jakarta.persistence.EntityExistsException;

class AuthenticationServiceTest extends TestContext {

    private final AuthenticationServiceImpl authenticationService;
    private final PasswordEncoder encoder;

    public AuthenticationServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationServiceImpl authenticationService,
            PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @BeforeEach
    void beforeEach() {
        getUserRepository().deleteAll();
    }

    private final UserEntity getUser() {
        return UserEntity
                .builder()
                .username(getRandomUsername())
                .password(getRandomPassword())
                .build();
    }

    private final UserEntity saveUser() {
        return getUserRepository().save(getUser());
    }

    @Test
    void authenticate() {
        UserEntity user = getUser();
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

        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
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
        authenticationService.registerAdmin(username, password);

        User user = getUserRepository().findAll().get(0);
        assertEquals(username, user.getUsername());
        assertTrue(encoder.matches(password, user.getPassword()));
        assertEquals("ADMIN", user.getRoles().stream().findFirst().get().getName());
    }

    @Test
    void registerAdmin_error_entityExists() {
        User user = saveUser();

        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
                () -> authenticationService.registerAdmin(user.getUsername(), user.getPassword()));
        assertEquals(getErrorUsernameDuplicated(user.getUsername()), exception.getMessage());
    }

    @Test
    void registerAdmin_error_passwordSecurity() {
        String password = "1234567itsq";

        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
                () -> authenticationService.registerAdmin(getRandomUsername(), password));
        assertEquals("Password is not strong!", exception.getMessage());
    }

}
