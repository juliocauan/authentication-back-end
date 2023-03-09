package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.ProfileRoles;
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
    private final String urlAlterUserRole = "/api/auth/profile";
    private final String urlGetAllUsers = "/api/auth/users";
    private final String headerAuthorization = "Authorization";

    private final String password = "1234567890";
    private final String username1 = "test1@email.com";
    private final String username2 = "test2@email.com";
    private final String username3 = "test3@email.com";
    private final int uuidSize = 36;

    private final String messageOk = "User registered successfully!";
    private final String errorDuplicatedUsername = "Username is already taken!";
    private final String validationError = "Input validation error!";
    private final String notAllowedError = "Not Allowed!";
    private final String userNotFoundError = "User Not Found with username: ";

    private final SigninForm signinForm = new SigninForm();
    private final SignupForm signupForm = new SignupForm();

    private String token;

    public AuthControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthController authController) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authController = authController;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        token = null;
        signupForm.username(username1).password(password).role(EnumRole.USER);
        signinForm.username(username1).password(password);
    }

    private final String getToken(String usernameToken, EnumRole role){
        SignupForm signup = new SignupForm().username(usernameToken).password(password).role(role);
        SigninForm signin = new SigninForm().username(usernameToken).password(password);
        authController._signupUser(signup);
        JWTResponse response = authController._signinUser(signin).getBody();
        return response == null ? null : "Bearer " + response.getToken();
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
            .andExpect(jsonPath("$.fieldErrors", hasSize(3)));
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

    @Test
    void givenLoggedUser_WhenProfileContent_ThenProfile() throws Exception{
        token = getToken(username1, EnumRole.USER);
        getMockMvc().perform(
            get(urlProfile)
                .header(headerAuthorization, token))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username1));
    }

    @Test
    void givenLoggedAdmin_WhenProfileContent_ThenProfile() throws Exception{
        token = getToken(username1, EnumRole.ADMIN);
        getMockMvc().perform(
            get(urlProfile)
                .header(headerAuthorization, token))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username1));
    }

    @Test
    void givenLoggedManager_WhenProfileContent_ThenProfile() throws Exception{
        token = getToken(username1, EnumRole.MANAGER);
        getMockMvc().perform(
            get(urlProfile)
                .header(headerAuthorization, token))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username1));
    }

    @Test
    void givenUnlogged_WhenProfileContent_ThenUnauthorized() throws Exception{
        getMockMvc().perform(
            get(urlProfile))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(notAllowedError));
    }

    @Test
    void givenNotAuthenticatedAdmin_WhenAlterUserRole_ThenForbidden() throws Exception{
        token = getToken(username1, EnumRole.USER);
        getMockMvc().perform(
            patch(urlAlterUserRole)
                .header(headerAuthorization, token))
            .andExpect(status().isForbidden());
        token = getToken(username2, EnumRole.MANAGER);
        getMockMvc().perform(
            patch(urlAlterUserRole)
                .header(headerAuthorization, token))
            .andExpect(status().isForbidden());
    }

    @Test
    void givenNotPresentUsername_WhenAlterUserRole_Then404AndMessage() throws Exception{
        token = getToken(username1, EnumRole.ADMIN);
        Set<EnumRole> roles = new HashSet<>();
        roles.add(EnumRole.USER);
        ProfileRoles profileRoles = new ProfileRoles().username(username2).roles(roles);
        getMockMvc().perform(
            patch(urlAlterUserRole)
                .header(headerAuthorization, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(profileRoles)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(userNotFoundError + username2))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void givenProfileRoles_WhenAlterUserRole_Then200AndProfileRoles() throws Exception{
        token = getToken(username2, EnumRole.ADMIN);
        authController._signupUser(signupForm);
        Set<EnumRole> roles = new HashSet<>();
        for(EnumRole role : EnumRole.values()) roles.add(role);
        ProfileRoles profileRoles = new ProfileRoles().username(username1).roles(roles);
        getMockMvc().perform(
            patch(urlAlterUserRole)
                .header(headerAuthorization, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(profileRoles)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value(username1))
            .andExpect(jsonPath("$.roles", hasSize(EnumRole.values().length)));
    }

    @Test
    void givenNotAuthenticatedAdmin_WhenGetAllUsers_ThenForbidden() throws Exception{
        token = getToken(username1, EnumRole.USER);
        getMockMvc().perform(
            get(urlGetAllUsers)
                .header(headerAuthorization, token))
            .andExpect(status().isForbidden());
        token = getToken(username2, EnumRole.MANAGER);
        getMockMvc().perform(
            get(urlGetAllUsers)
                .header(headerAuthorization, token))
            .andExpect(status().isForbidden());
    }

    @Test
    void givenNothing_WhenGetAllUsers_Then200AndList() throws Exception{
        getToken(username1, EnumRole.MANAGER);
        getToken(username2, EnumRole.USER);
        token = getToken(username3, EnumRole.ADMIN);
        String nullString = null;
        getMockMvc().perform(
            get(urlGetAllUsers)
                .header(headerAuthorization, token)
                .queryParam("username", nullString)
                .queryParam("role", nullString))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$.[0].id", Matchers.hasLength(uuidSize)))
            .andExpect(jsonPath("$.[0].username", Matchers.containsString("test")))
            .andExpect(jsonPath("$.[0].roles", hasSize(1)));
    }

    @Test
    void givenUsername_WhenGetAllUsers_Then200AndList() throws Exception{
        token = getToken(username1, EnumRole.ADMIN);
        getToken(username2, EnumRole.MANAGER);
        getToken(username3, EnumRole.USER);
        String nullString = null;
        getMockMvc().perform(
            get(urlGetAllUsers)
                .header(headerAuthorization, token)
                .queryParam("username", "1")
                .queryParam("role", nullString))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id", Matchers.hasLength(uuidSize)))
            .andExpect(jsonPath("$.[0].username").value(username1))
            .andExpect(jsonPath("$.[0].roles[0]").value(EnumRole.ADMIN.getValue()));
    }

    @Test
    void givenRole_WhenGetAllUsers_Then200AndList() throws Exception{
        token = getToken(username1, EnumRole.ADMIN);
        getToken(username2, EnumRole.MANAGER);
        getToken(username3, EnumRole.USER);
        getMockMvc().perform(
            get(urlGetAllUsers)
                .header(headerAuthorization, token)
                .queryParam("role", EnumRole.MANAGER.getValue()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id", Matchers.hasLength(uuidSize)))
            .andExpect(jsonPath("$.[0].username").value(username2))
            .andExpect(jsonPath("$.[0].roles[0]").value(EnumRole.MANAGER.getValue()));
    }

    @Test
    void givenUsernameAndRole_WhenGetAllUsers_Then200AndList() throws Exception{
        token = getToken(username1, EnumRole.ADMIN);
        getToken(username2, EnumRole.MANAGER);
        getToken(username3, EnumRole.USER);
        getMockMvc().perform(
            get(urlGetAllUsers)
                .header(headerAuthorization, token)
                .queryParam("username", "1")
                .queryParam("role", EnumRole.USER.getValue()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isEmpty());
    }

}