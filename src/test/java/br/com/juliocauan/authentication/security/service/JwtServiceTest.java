package br.com.juliocauan.authentication.security.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.security.service.JwtService;
import jakarta.persistence.EntityExistsException;

public class JwtServiceTest extends TestContext {

    private final JwtService jwtService;
    private final SignupForm signupForm = new SignupForm();
    private final SigninForm signinForm = new SigninForm();

    private final String password = "12345678";
    private final String username = "test@email.com";
    private final String errorUsernameDuplicated = "Username is already taken!";

    private UserEntity entity;

    public JwtServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, JwtService jwtService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.jwtService = jwtService;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        signupForm.username(username).password(password);
        signinForm.username(username).password(password);
        entity = UserEntity.builder()
            .password(password)
            .username(username)
            .roles(null)
        .build();
    }

    @Test
    public void givenValidSignupForm_WhenValidateAndRegisterNewUser_ThenDoesNotThrow(){
        Assertions.assertDoesNotThrow(() -> jwtService.validateAndRegisterNewUser(signupForm));
        Assertions.assertEquals(1, getUserRepository().findAll().size());
    }
    
    @Test
    public void givenInvalidSignupForm_WhenValidateAndRegisterNewUser_ThenDuplicatedUsername(){
        getUserRepository().save(entity);
        Assertions.assertThrows(EntityExistsException.class, () -> jwtService.validateAndRegisterNewUser(signupForm),
            errorUsernameDuplicated);
    }

    @Test
    public void givenValidSigninForm_WhenAuthenticate_ThenJWTResponse(){
        jwtService.validateAndRegisterNewUser(signupForm);
        Assertions.assertInstanceOf(JWTResponse.class, jwtService.authenticate(signinForm));
    }

    @Test
    public void givenInvalidSigninForm_WhenAuthenticate_ThenUnauthorized(){
        Assertions.assertThrows(Exception.class, () -> jwtService.authenticate(signinForm));
    }

}
