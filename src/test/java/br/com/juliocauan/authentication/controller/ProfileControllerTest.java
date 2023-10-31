package br.com.juliocauan.authentication.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.PasswordUpdateForm;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;

class ProfileControllerTest extends TestContext {

    private final AuthenticationServiceImpl authenticationService;
    private final PasswordEncoder encoder;

    private final String urlProfile = "/api/auth/profile";
    private final String authorizationHeader = "Authorization";

    private final String username = "test@email.com";
    private final String password = "1234567890";
    private final String newPassword = "1234567890123";

    private final String updatedPasswordMessage = "Password updated successfully!";
    private final String errorPassword = "Wrong current password!";
    private final String errorPasswordMatch = "Confirmation and new password are different!";
    private final String errorNotAuthorized = "Full authentication is required to access this resource";

    public ProfileControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationServiceImpl authenticationService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        getUserRepository().save(UserEntity
            .builder()
                .id(null)
                .username(username)
                .password(encoder.encode(password))
                .roles(new HashSet<>())
            .build());
    }

    private final String getToken(){
        JWTResponse jwt = authenticationService.authenticate(username, password);
        return jwt.getType() + " " + jwt.getToken();
    }

    @Test
    void profileContent() throws Exception{
        getMockMvc().perform(
            get(urlProfile)
                .header(authorizationHeader, getToken()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void profileContent_error_notLogged() throws Exception{
        getMockMvc().perform(
            get(urlProfile))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void updateUserPassword() throws Exception{
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(password)
            .newPasswordMatch(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(newPassword));
        getMockMvc().perform(
            patch(urlProfile)
                .header(authorizationHeader, getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdateForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body").value(updatedPasswordMessage));
    }

    @Test
    void updateUserPassword_error_notLogged() throws Exception{
        getMockMvc().perform(
            patch(urlProfile))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void updateUserPassword_error_passwordMatch() throws Exception{
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(password)
            .newPasswordMatch(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(password));
        getMockMvc().perform(
            patch(urlProfile)
                .header(authorizationHeader, getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdateForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorPasswordMatch));
    }

    @Test
    void updateUserPassword_error_invalidPassword() throws Exception{
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(newPassword)
            .newPasswordMatch(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(newPassword));
        getMockMvc().perform(
            patch(urlProfile)
                .header(authorizationHeader, getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdateForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorPassword));
    }

}
