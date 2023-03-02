package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumToken;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class AuthControllerTest extends TestContext {

    private final AuthController authController;

    private final String urlSignup = "/api/auth/signup";
    private final String urlSignin = "/api/auth/signin";
    private final String urlProfile = "/api/auth/profile";

    private final String password = "1234567890";
    private final String username = "test@email.com";

    private final String usernameNotPresent = "test2@email.com";

    private final String messageOk = "User registered successfully!";
    private final String errorDuplicatedUsername = "Username is already taken!";

    private final SignupForm signupForm = new SignupForm();
    private final SigninForm signinForm = new SigninForm();

    public AuthControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthController authController) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authController = authController;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        signupForm.password(password).username(username);
        signinForm.username(username).password(password);
    }

    @Test
    public void givenValidSignupForm_WhenSignupUser_Then200AndMessage() throws Exception{
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(messageOk));
    }

    @Test
    public void givenDuplicatedUsername_WhenSignupUser_ThenEntityExistsException() throws Exception{
        getUserRepository().save(UserEntity.builder().password(password).username(username).roles(null).build());
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorDuplicatedUsername));
    }

    @Test
    public void givenValidSigninForm_WhenAuthenticate_ThenJwtResponse() throws Exception{
        authController._signupUser(signupForm);

        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", hasLength(143)))
            .andExpect(jsonPath("$.type").value(EnumToken.BEARER.getValue()))
            .andExpect(jsonPath("$.username").value(username))
            .andExpect(jsonPath("$.roles", hasSize(1)));
    }

    @Test
    public void givenInvalidSigninForm_WhenAuthenticate_ThenUnauthorized() throws Exception{
        signinForm.username(usernameNotPresent).password(password);
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Not Allowed!"));
    }

    @Test
    public void givenLoggedUser_WhenProfileContent_ThenProfile() throws Exception{
        authController._signupUser(signupForm);
        authController._signinUser(signinForm);
        getMockMvc().perform(
            get(urlProfile))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username));
    }

}
