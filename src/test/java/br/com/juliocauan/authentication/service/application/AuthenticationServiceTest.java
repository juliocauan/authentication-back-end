package br.com.juliocauan.authentication.service.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;
import jakarta.persistence.EntityExistsException;

class AuthenticationServiceTest extends TestContext {

    private final AuthenticationServiceImpl authenticationService;
    private final PasswordEncoder encoder;

    private final String password = "12345678";
    private final String username = "test@email.com";
    private final String errorUsernameDuplicated = "Username is already taken!";
    private final String errorBadCredentials = "Bad credentials";

    public AuthenticationServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationServiceImpl authenticationService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
    }

    @Test
    void validateAndRegisterNewUser(){
        assertDoesNotThrow(() -> authenticationService.validateAndRegisterNewUser(username, password, EnumRole.MANAGER));
        assertEquals(1, getUserRepository().findAll().size());

        UserEntity user = getUserRepository().findAll().get(0);
        assertEquals(username, user.getUsername());
        assertTrue(encoder.matches(password, user.getPassword()));
        assertEquals(EnumRole.MANAGER, user.getRoles().iterator().next().getName());
    }

    @Test
    void validateAndRegisterNewUser_NullRole(){
        assertDoesNotThrow(() -> authenticationService.validateAndRegisterNewUser(username, password, null));

        UserEntity user = getUserRepository().findAll().get(0);
        assertEquals(EnumRole.USER, user.getRoles().iterator().next().getName());
    }
    
    @Test
    void validateAndRegisterNewUser_duplicatedUserError(){
        getUserRepository().save(UserEntity.builder()
            .id(null)
            .username(username)
            .password(password)
            .roles(null)
        .build());

        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
            () -> authenticationService.validateAndRegisterNewUser(username, password, null));
        assertEquals(errorUsernameDuplicated, exception.getMessage());
    }

    @Test
    void authenticate(){
        authenticationService.validateAndRegisterNewUser(username, password, null);
        JWTResponse response = authenticationService.authenticate(username, password);
        assertEquals("Bearer", response.getType());
        assertNotNull(response.getToken());
    }

    @Test
    void authenticate_badCredentialsError(){
        BadCredentialsException exception = assertThrowsExactly(BadCredentialsException.class,
            () -> authenticationService.authenticate(username, password));
        assertEquals(errorBadCredentials, exception.getMessage());
    }

}
