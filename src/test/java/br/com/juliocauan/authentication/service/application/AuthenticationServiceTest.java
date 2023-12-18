package br.com.juliocauan.authentication.service.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.JWT;
import org.openapitools.model.PasswordMatch;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;
import jakarta.persistence.EntityExistsException;

class AuthenticationServiceTest extends TestContext {

    private final AuthenticationServiceImpl authenticationService;
    private final PasswordEncoder encoder;

    private final String username = getRandomUsername();
    private final String passwordRandom = getRandomPassword();
    private final String errorBadCredentials = "Bad credentials";

    private final String roleManager = "MANAGER";
    private final String roleUser = "USER";
    private final PasswordMatch password = new PasswordMatch();

    public AuthenticationServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationServiceImpl authenticationService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @Override
    public void setup() {
        super.setup();
        getRoleRepository().save(RoleEntity.builder().name(roleManager).build());
        getRoleRepository().save(RoleEntity.builder().name(roleUser).build());
        password.password(passwordRandom).passwordConfirmation(passwordRandom);
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
    }

    @Test
    void registerUser(){
        assertDoesNotThrow(() -> authenticationService.registerUser(username, password));
        assertEquals(1, getUserRepository().findAll().size());

        UserEntity user = getUserRepository().findAll().get(0);
        assertEquals(username, user.getUsername());
        assertTrue(encoder.matches(password.getPassword(), user.getPassword()));
    }

    @Test
    void registerUser_duplicatedUserError(){
        getUserRepository().save(UserEntity.builder()
            .id(null)
            .username(username)
            .password(password.getPassword())
        .build());

        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
            () -> authenticationService.registerUser(username, password));
        assertEquals(getErrorUsernameDuplicated(username), exception.getMessage());
    }

    @Test
    void authenticate(){
        authenticationService.registerUser(username, password);
        JWT response = authenticationService.authenticate(username, passwordRandom);
        assertTrue(response.getToken().contains("."));
    }

    @Test
    void authenticate_badCredentialsError(){
        BadCredentialsException exception = assertThrowsExactly(BadCredentialsException.class,
            () -> authenticationService.authenticate(username, passwordRandom));
        assertEquals(errorBadCredentials, exception.getMessage());
    }

}
