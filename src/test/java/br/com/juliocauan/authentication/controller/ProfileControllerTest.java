package br.com.juliocauan.authentication.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.ApiError;
import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.PasswordUpdate;
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
    private final String newPassword = "1234567890123";
    private final String username1 = "test1@email.com";

    private final String updatedPasswordMessage = "Password updated successfully!";
    private final String oldPasswordError = "Wrong old password!";
    private final String newPasswordError = "Confirmation and new password are different!";
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
    void givenAnyLoggedUser_WhenProfileContent_ThenProfile() throws Exception{
        for(EnumRole role : EnumRole.values()){
            getUserRepository().deleteAll();
            token = getToken(username1, role);
            getMockMvc().perform(
                get(urlProfile)
                    .header(headerAuthorization, token))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username1));
        }
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
    void givenAnyLoggedUser_WhenAlterUserPassword_ThenMessage() throws Exception{
        PasswordUpdate passwordUpdate = new PasswordUpdate()
            .oldPassword(password)
            .newPassword(newPassword)
            .newPasswordConfirmation(newPassword);
        for(EnumRole role : EnumRole.values()){
            getUserRepository().deleteAll();
            token = getToken(username1, role);
            getMockMvc().perform(
                patch(urlProfile)
                    .header(headerAuthorization, token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getObjectMapper().writeValueAsString(passwordUpdate)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(updatedPasswordMessage));
        }
    }

    @Test
    void givenUnlogged_WhenAlterUserPassword_ThenUnauthorized() throws Exception{
        getMockMvc().perform(
            patch(urlProfile))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(notAllowedError));
    }

    @Test
    void givenWrongOldPassword_WhenAlterUserPassword_ThenErrorMessage() throws Exception{
        PasswordUpdate passwordUpdate = new PasswordUpdate()
            .oldPassword(newPassword)
            .newPassword(newPassword)
            .newPasswordConfirmation(newPassword);
        token = getToken(username1, EnumRole.USER);
        getMockMvc().perform(
            patch(urlProfile)
                .header(headerAuthorization, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdate)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(oldPasswordError));
    }

    @Test
    void givenWrongConfirmationPassword_WhenAlterUserPassword_ThenErrorMessage() throws Exception{
        PasswordUpdate passwordUpdate = new PasswordUpdate()
            .oldPassword(password)
            .newPassword(newPassword)
            .newPasswordConfirmation(password);
        token = getToken(username1, EnumRole.USER);
        getMockMvc().perform(
            patch(urlProfile)
                .header(headerAuthorization, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdate)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(newPasswordError));
    }
    
    @Test
    void givenLoggedUser_WhenAlterUserPassword_CheckIfSucceded() throws Exception{
        PasswordUpdate passwordUpdate = new PasswordUpdate()
            .oldPassword(password)
            .newPassword(newPassword)
            .newPasswordConfirmation(newPassword);
            getUserRepository().deleteAll();
        token = getToken(username1, EnumRole.USER);
        getMockMvc().perform(
            patch(urlProfile)
                .header(headerAuthorization, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdate)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(updatedPasswordMessage));
        
        SigninForm signinForm = new SigninForm().username(username1).password(newPassword);
        Assertions.assertInstanceOf(JWTResponse.class, authController._signinUser(signinForm).getBody());

        signinForm.password(password);
        Assertions.assertInstanceOf(ApiError.class, authController._signinUser(signinForm).getBody());
    }
}
