package br.com.juliocauan.authentication.service.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.JwtServiceImpl;
import jakarta.persistence.EntityExistsException;

class JwtServiceTest extends TestContext {

    private final JwtServiceImpl jwtService;
    private final PasswordEncoder encoder;

    private final String password = "12345678";
    private final String username = "test@email.com";
    private final String errorUsernameDuplicated = "Username is already taken!";
    private final String errorBadCredentials = "Bad credentials";

    public JwtServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, JwtServiceImpl jwtService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
    }

    @Test
    void validateAndRegisterNewUser(){
        Assertions.assertDoesNotThrow(() -> jwtService.validateAndRegisterNewUser(username, password, EnumRole.MANAGER));
        Assertions.assertEquals(1, getUserRepository().findAll().size());

        UserEntity user = getUserRepository().findAll().get(0);
        Assertions.assertEquals(username, user.getUsername());
        Assertions.assertTrue(encoder.matches(password, user.getPassword()));
        Assertions.assertEquals(EnumRole.MANAGER, user.getRoles().iterator().next().getName());
    }

    @Test
    void validateAndRegisterNewUser_NullRole(){
        Assertions.assertDoesNotThrow(() -> jwtService.validateAndRegisterNewUser(username, password, null));

        UserEntity user = getUserRepository().findAll().get(0);
        Assertions.assertEquals(EnumRole.USER, user.getRoles().iterator().next().getName());
    }
    
    @Test
    void validateAndRegisterNewUser_DuplicatedUserError(){
        getUserRepository().save(UserEntity.builder()
            .id(null)
            .username(username)
            .password(password)
            .roles(null)
        .build());

        EntityExistsException exception = Assertions.assertThrowsExactly(EntityExistsException.class,
            () -> jwtService.validateAndRegisterNewUser(username, password, null));
        Assertions.assertEquals(errorUsernameDuplicated, exception.getMessage());
    }

    @Test
    void authenticate(){
        jwtService.validateAndRegisterNewUser(username, password, null);
        JWTResponse response = jwtService.authenticate(username, password);
        Assertions.assertEquals("Bearer", response.getType());
        Assertions.assertNotNull(response.getToken());
    }

    @Test
    void authenticate_BadCredentialsError(){
        BadCredentialsException exception = Assertions.assertThrowsExactly(BadCredentialsException.class,
            () -> jwtService.authenticate(username, password));
        Assertions.assertEquals(errorBadCredentials, exception.getMessage());
    }

}
