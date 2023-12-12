package br.com.juliocauan.authentication.service.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.JWT;
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
    private final String password = getRandomPassword();
    private final String errorUsernameDuplicated = "Username is already taken!";
    private final String errorBadCredentials = "Bad credentials";

    private final String roleManager = "MANAGER";
    private final String roleUser = "USER";

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
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
    }

    @Test
    void registerUser(){
        assertDoesNotThrow(() -> authenticationService.registerUser(username, password, roleManager));
        assertEquals(1, getUserRepository().findAll().size());

        UserEntity user = getUserRepository().findAll().get(0);
        assertEquals(username, user.getUsername());
        assertTrue(encoder.matches(password, user.getPassword()));
        assertEquals(roleManager, user.getRoles().iterator().next().getName());
    }

    @Test
    void registerUser_NullRole(){
        assertDoesNotThrow(() -> authenticationService.registerUser(username, password, null));

        UserEntity user = getUserRepository().findAll().get(0);
        assertEquals(roleUser, user.getRoles().iterator().next().getName());
    }
    
    @Test
    void registerUser_duplicatedUserError(){
        getUserRepository().save(UserEntity.builder()
            .id(null)
            .username(username)
            .password(password)
            .roles(null)
        .build());

        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
            () -> authenticationService.registerUser(username, password, null));
        assertEquals(errorUsernameDuplicated, exception.getMessage());
    }

    @Test
    void authenticate(){
        authenticationService.registerUser(username, password, null);
        JWT response = authenticationService.authenticate(username, password);
        assertTrue(response.getToken().contains("."));
    }

    @Test
    void authenticate_badCredentialsError(){
        BadCredentialsException exception = assertThrowsExactly(BadCredentialsException.class,
            () -> authenticationService.authenticate(username, password));
        assertEquals(errorBadCredentials, exception.getMessage());
    }

}
