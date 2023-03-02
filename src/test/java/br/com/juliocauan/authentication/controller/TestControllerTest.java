package br.com.juliocauan.authentication.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class TestControllerTest extends TestContext {

    private final AuthController authController;

    private final String header = "Authorization";
    private final String urlAll = "/api/test/all";
    private final String urlAdmin = "/api/test/admin";
    private final String urlManager = "/api/test/manager";
    private final String urlUser = "/api/test/user";

    private final String allOkMessage = "Public Content";
    private final String adminOkMessage = "Admin Board";
    private final String managerOkMessage = "Manager Board";
    private final String userOkMessage = "User Board";
    private final String notAllowedMessage = "Not Allowed!";

    private final String adminUsername = "admin@email.com";
    private final String managerUsername = "manager@email.com";
    private final String userUsername = "user@email.com";
    private final String password = "1234567890";

    private String adminToken;
    private String managerToken;
    private String userToken;

    public TestControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthController authController) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authController = authController;
    }

    @Override @BeforeAll
    public void setup() {
        super.setup();
        adminToken = signupAndSignin(adminUsername, EnumRole.ADMIN);
        managerToken = signupAndSignin(managerUsername, EnumRole.MANAGER);
        userToken = signupAndSignin(userUsername, EnumRole.USER);
    }

    private final String signupAndSignin(String username, EnumRole role) {
        SignupForm signupForm = new SignupForm();
        SigninForm signinForm = new SigninForm();
        Set<EnumRole> roles = new HashSet<>();
        JWTResponse body = new JWTResponse();
        roles.add(role);
        signupForm.username(username).password(password).roles(roles);
        signinForm.username(username).password(password);
        authController._signupUser(signupForm);
        body = authController._signinUser(signinForm).getBody();
        return body == null ? null : body.getType() + " " + body.getToken();
    }

    @Test
    public void givenNonAuthenticatedUser_WhenAllAccess_ThenOkMessage() throws Exception {
        getMockMvc().perform(
                get(urlAll))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(allOkMessage));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void givenAuthenticatedAdmin_WhenAdminAccess_ThenAdminOkMessage() throws Exception {
        getMockMvc().perform(
                get(urlAdmin))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(adminOkMessage));
    }

    @Test
    public void givenAuthenticatedUserOrManager_WhenAdminAccess_ThenForbidden() throws Exception {
        getMockMvc().perform(
                get(urlAdmin)
                        .header(header, userToken))
                .andDo(print())
                .andExpect(status().isForbidden());
        getMockMvc().perform(
                get(urlAdmin)
                        .header(header, managerToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"MANAGER"})
    public void givenAuthenticatedManager_WhenManagerAccess_ThenManagerOkMessage() throws Exception {
        getMockMvc().perform(
                get(urlManager))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(managerOkMessage));
    }

    @Test
    public void givenAuthenticatedUserOrAdmin_WhenManagerAccess_ThenForbidden() throws Exception {
        getMockMvc().perform(
                get(urlManager)
                        .header(header, userToken))
                .andDo(print())
                .andExpect(status().isForbidden());
        getMockMvc().perform(
                get(urlManager)
                        .header(header, adminToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenAuthenticatedUser_WhenUserAccess_ThenUserOkMessage() throws Exception {
        getMockMvc().perform(
                get(urlUser)
                        .header(header, userToken))
                .andDo(print())
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userOkMessage));
    }

    @Test
    @WithMockUser(authorities = {"MANAGER", "ADMIN"})
    public void givenAuthenticatedManagerOrAdmin_WhenUserAccess_ThenForbidden() throws Exception {
        getMockMvc().perform(
                get(urlUser))
                .andDo(print())
                .andExpect(status().isForbidden());
        getMockMvc().perform(
                get(urlUser))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenNonAuthenticated_WhenUserAccess_ThenUnauthorized() throws Exception {
        getMockMvc().perform(
                get(urlUser))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(notAllowedMessage));
    }

}
