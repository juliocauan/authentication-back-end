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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class TestControllerTest extends TestContext {

    private final AuthController authController;

    private final String header = "Authorization";
    private final String urlAll = "/api/test/all";
    private final String urlAdmin = "/api/test/" + EnumRole.ADMIN;
    private final String urlManager = "/api/test/" + EnumRole.MANAGER;
    private final String urlUser = "/api/test/" + EnumRole.USER;

    private final String allOkMessage = "Public Content";
    private final String adminOkMessage = EnumRole.ADMIN + " Board";
    private final String managerOkMessage = EnumRole.MANAGER + " Board";
    private final String userOkMessage = EnumRole.USER + " Board";
    private final String errorMessage = "";

    private final String adminEmail = "admin@email.com";
    private final String adminUsername = "adminTest";
    private final String managerEmail = "manager@email.com";
    private final String managerUsername = "managerTest";
    private final String userEmail = "user@email.com";
    private final String userUsername = "userTest";
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
        adminToken = signupAndSignin(adminEmail, adminUsername, EnumRole.ADMIN);
        managerToken = signupAndSignin(managerEmail, managerUsername, EnumRole.MANAGER);
        userToken = signupAndSignin(userEmail, userUsername, EnumRole.USER);
    }
    
    private final String signupAndSignin(String email, String username, EnumRole role) {
        SignupForm signupForm = new SignupForm();
        SigninForm signinForm = new SigninForm();
        Set<EnumRole> roles = new HashSet<>();
        JWTResponse body = new JWTResponse();
        roles.add(role);
        signupForm.email(email).username(username).password(password).roles(roles);
        signinForm.username(username).password(password);
        authController._signupUser(signupForm);
        body = authController._signinUser(signinForm).getBody();
        return body == null ? null : body.getType() + " " + body.getToken();
    }

    @Test
    public void givenNonAuthenticatedUser_WhenAllAccess_ThenOkMessage() throws Exception{
        getMockMvc().perform(
            get(urlAll))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(allOkMessage));
    }
    
    @Test
    public void givenAuthenticatedAdmin_WhenRoleAccess_ThenOkMessage() throws Exception{
        getMockMvc().perform(
            get(urlAdmin)
                .header(header, adminToken))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(adminOkMessage));
    }
    
}
