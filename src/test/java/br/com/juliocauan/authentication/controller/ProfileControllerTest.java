package br.com.juliocauan.authentication.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
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

class ProfileControllerTest extends TestContext {

    private final AuthController authController;

    private final String urlProfile = "/api/auth/profile";
    private final String headerAuthorization = "Authorization";

    private final String password = "1234567890";
    private final String username1 = "test1@email.com";

    private final String notAllowedError = "Not Allowed!";

    private String token;

    public ProfileControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
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
    
}
