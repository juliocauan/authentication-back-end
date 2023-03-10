package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class AuthControllerTest extends TestContext {

    private final AuthController authController;

    private final String urlSignup = "/api/auth/signup";
    private final String urlSignin = "/api/auth/signin";

    private final String password = "1234567890";
    private final String username1 = "test1@email.com";
    private final String username2 = "test2@email.com";

    private final String messageOk = "User registered successfully!";
    private final String errorDuplicatedUsername = "Username is already taken!";
    private final String validationError = "Input validation error!";
    private final String notAllowedError = "Not Allowed!";

    private final SigninForm signinForm = new SigninForm();
    private final SignupForm signupForm = new SignupForm();

    public AuthControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthController authController) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authController = authController;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        signupForm.username(username1).password(password).role(EnumRole.USER);
        signinForm.username(username1).password(password);
    }

    @Test
    void givenInvalidSignupForm_WhenSignupUser_Then400AndResponse() throws Exception{
        signupForm.username("aaaaaaaaaaaaa").password("12345").role(null);
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(validationError))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors", hasSize(2)));
    }

    @Test
    void givenSignupForm_WhenSignupUser_Then200AndMessage() throws Exception{
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").value(messageOk));
    }

    @Test
    void givenDuplicatedUsername_WhenSignupUser_ThenEntityExistsException() throws Exception{
        getUserRepository().save(UserEntity.builder().password(password).username(username1).roles(null).build());
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorDuplicatedUsername))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void givenInvalidSigninForm_WhenSigninUser_Then400AndResponse() throws Exception{
        signinForm.username("aaaaaaaaaaaaa").password("12345");
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(validationError))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors", hasSize(2)));
    }

    @Test
    void givenSigninForm_WhenSigninUser_ThenJwtResponseWithUserRole() throws Exception{
        authController._signupUser(signupForm);
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", hasLength(144)))
            .andExpect(jsonPath("$.type").value("Bearer"))
            .andExpect(jsonPath("$.username").value(username1))
            .andExpect(jsonPath("$.roles", hasSize(1)))
            .andExpect(jsonPath("$.roles[0]").value(EnumRole.USER.getValue()));
    }

    @Test
    void givenCustomSigninForm_WhenSigninUser_ThenJwtResponseWithAdminRole() throws Exception{
        authController._signupUser(signupForm.role(EnumRole.ADMIN));
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", hasLength(144)))
            .andExpect(jsonPath("$.type").value("Bearer"))
            .andExpect(jsonPath("$.username").value(username1))
            .andExpect(jsonPath("$.roles", hasSize(1)))
            .andExpect(jsonPath("$.roles[0]").value(EnumRole.ADMIN.getValue()));
    }

    @Test
    void givenNotPresentUsernameSigninForm_WhenSigninUser_ThenUnauthorized() throws Exception{
        signinForm.username(username2).password(password);
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(notAllowedError));
    }

}