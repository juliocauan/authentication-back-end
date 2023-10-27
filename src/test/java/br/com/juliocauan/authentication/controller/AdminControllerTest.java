package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.AlterUserRolesForm;
import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class AdminControllerTest extends TestContext {

    private final AuthController authController;

    private final String urlAdmin = "/api/auth/admin";
    private final String headerAuthorization = "Authorization";

    private final String password = "1234567890";
    private final String username1 = "test1@email.com";
    private final String username2 = "test2@email.com";
    private final String username3 = "test3@email.com";
    private final int uuidSize = 36;
    
    private final String userNotFoundError = "User Not Found with username: ";

    private String token;

    public AdminControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthController authController) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authController = authController;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        token = null;
    }

    private final String getToken(String usernameToken, EnumRole role){
        SignupForm signup = new SignupForm().username(usernameToken).password(password).role(role);
        SigninForm signin = new SigninForm().username(usernameToken).password(password);
        authController._signupUser(signup);
        JWTResponse response = authController._signinUser(signin).getBody();
        return response == null ? null : "Bearer " + response.getToken();
    }

    @Test
    void givenNotAuthenticatedAdmin_WhenAlterUserRole_ThenForbidden() throws Exception{
        token = getToken(username1, EnumRole.USER);
        getMockMvc().perform(
            patch(urlAdmin)
                .header(headerAuthorization, token))
            .andExpect(status().isForbidden());
        token = getToken(username2, EnumRole.MANAGER);
        getMockMvc().perform(
            patch(urlAdmin)
                .header(headerAuthorization, token))
            .andExpect(status().isForbidden());
    }

    @Test
    void givenNotPresentUsername_WhenAlterUserRole_Then404AndMessage() throws Exception{
        token = getToken(username1, EnumRole.ADMIN);
        Set<EnumRole> roles = new HashSet<>();
        roles.add(EnumRole.USER);
        AlterUserRolesForm alterUserRolesForm = new AlterUserRolesForm().username(username2).roles(roles);
        getMockMvc().perform(
            patch(urlAdmin)
                .header(headerAuthorization, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(alterUserRolesForm)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(userNotFoundError + username2))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void givenProfileRoles_WhenAlterUserRole_Then200AndProfileRoles() throws Exception{
        getToken(username1, EnumRole.USER);
        token = getToken(username2, EnumRole.ADMIN);
        Set<EnumRole> roles = new HashSet<>();
        for(EnumRole role : EnumRole.values()) roles.add(role);
        AlterUserRolesForm alterUserRolesForm = new AlterUserRolesForm().username(username1).roles(roles);
        getMockMvc().perform(
            patch(urlAdmin)
                .header(headerAuthorization, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(alterUserRolesForm)))
            .andExpect(status().isOk());
            // TODO review
            // .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            // .andExpect(jsonPath("$.username").value(username1))
            // .andExpect(jsonPath("$.roles", hasSize(EnumRole.values().length)));
    }

    @Test
    void givenNotAuthenticatedAdmin_WhenGetAllUsers_ThenForbidden() throws Exception{
        token = getToken(username1, EnumRole.USER);
        getMockMvc().perform(
            get(urlAdmin)
                .header(headerAuthorization, token))
            .andExpect(status().isForbidden());
        token = getToken(username2, EnumRole.MANAGER);
        getMockMvc().perform(
            get(urlAdmin)
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
            get(urlAdmin)
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
            get(urlAdmin)
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
            get(urlAdmin)
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
            get(urlAdmin)
                .header(headerAuthorization, token)
                .queryParam("username", "1")
                .queryParam("role", EnumRole.USER.getValue()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isEmpty());
    }
    
}
