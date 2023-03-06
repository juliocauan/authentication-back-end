package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.JWTResponse;
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
    private final String urlProfile = "/api/auth/profile";
    private final String headerAuthorization = "Authorization";

    private final String password = "1234567890";
    private final String username = "test@email.com";

    private final String usernameNotPresent = "test2@email.com";

    private final String messageOk = "User registered successfully!";
    private final String errorDuplicatedUsername = "Username is already taken!";
    private final String validationError = "Input validation error!";

    private final SigninForm signinForm = new SigninForm();
    private final SignupForm signupForm = new SignupForm();

    private String token;
    private JWTResponse response;

    public AuthControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthController authController) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authController = authController;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        signupForm.password(password).username(username).role(EnumRole.USER);
        signinForm.username(username).password(password);
    }

    @Test
    void givenInvalidFormatSignupForm_WhenSignupUser_Then400AndResponse() throws Exception{
        signupForm.username("aaaaaaaaaaaaa").password("12345").role(null);
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(validationError))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors", hasSize(3)));
    }

    @Test
    void givenValidSignupForm_WhenSignupUser_Then200AndMessage() throws Exception{
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
        getUserRepository().save(UserEntity.builder().password(password).username(username).roles(null).build());
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorDuplicatedUsername));
    }

    @Test
    void givenInvalidFormatSigninForm_WhenSignupUser_Then400AndResponse() throws Exception{
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
    void givenStandardValidSigninForm_WhenAuthenticate_ThenJwtResponseWithUserRole() throws Exception{
        authController._signupUser(signupForm);
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", hasLength(143)))
            .andExpect(jsonPath("$.type").value("Bearer"))
            .andExpect(jsonPath("$.username").value(username))
            .andExpect(jsonPath("$.roles", hasSize(1)))
            .andExpect(jsonPath("$.roles[0]").value(EnumRole.USER.getValue()));
    }

    @Test
    void givenCustomValidSigninForm_WhenAuthenticate_ThenJwtResponseWithAdminRole() throws Exception{
        signupForm.role(EnumRole.ADMIN);
        authController._signupUser(signupForm);
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", hasLength(143)))
            .andExpect(jsonPath("$.type").value("Bearer"))
            .andExpect(jsonPath("$.username").value(username))
            .andExpect(jsonPath("$.roles", hasSize(1)))
            .andExpect(jsonPath("$.roles[0]").value(EnumRole.ADMIN.getValue()));
    }

    @Test
    void givenInvalidSigninForm_WhenAuthenticate_ThenUnauthorized() throws Exception{
        signinForm.username(usernameNotPresent).password(password);
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Not Allowed!"));
    }

    @Test
    void givenLoggedUser_WhenProfileContent_ThenProfile() throws Exception{
        authController._signupUser(signupForm);
        response = authController._signinUser(signinForm).getBody();
        token = response == null ? null : response.getToken();
        getMockMvc().perform(
            get(urlProfile)
                .header(headerAuthorization, token))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void givenLoggedAdmin_WhenProfileContent_ThenProfile() throws Exception{
        signupForm.role(EnumRole.ADMIN);
        authController._signupUser(signupForm);
        response = authController._signinUser(signinForm).getBody();
        token = response == null ? null : response.getToken();
        getMockMvc().perform(
            get(urlProfile)
                .header(headerAuthorization, token))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void givenLoggedManager_WhenProfileContent_ThenProfile() throws Exception{
        signupForm.role(EnumRole.MANAGER);
        authController._signupUser(signupForm);
        response = authController._signinUser(signinForm).getBody();
        token = response == null ? null : response.getToken();
        getMockMvc().perform(
            get(urlProfile)
                .header(headerAuthorization, token))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void givenUnlogged_WhenProfileContent_ThenUnauthorized() throws Exception{
        getMockMvc().perform(
            get(urlProfile))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Not Allowed!"));
    }

}
