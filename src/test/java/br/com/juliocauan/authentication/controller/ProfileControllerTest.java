package br.com.juliocauan.authentication.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    private final String url = "/profile";
    private final String authorizationHeader = "Authorization";

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();
    private final String newPassword = getRandomPassword();

    private final String updatedPasswordMessage = "Password updated successfully!";
    private final String invalidPasswordError = "Passwords don't match!";
    private final String errorNotAuthorized = "Full authentication is required to access this resource";

    public ProfileControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationServiceImpl authenticationService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        getUserRepository().save(UserEntity
            .builder()
                .id(null)
                .username(username)
                .password(encoder.encode(password))
                .roles(null)
            .build());
    }

    private final String getBearerToken(){
        return "Bearer " + authenticationService.authenticate(username, password).getToken();
    }

    @Test
    void updateUserPassword() throws Exception{
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(password)
            .match(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(newPassword));
        
        getMockMvc().perform(
            patch(url)
                .header(authorizationHeader, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdateForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(updatedPasswordMessage));
    }

    @Test
    void updateUserPassword_error_unauthorized() throws Exception{
        getMockMvc().perform(
            patch(url))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void updateUserPassword_error_passwordMatch() throws Exception{
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(password)
            .match(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(password));
        
        getMockMvc().perform(
            patch(url)
                .header(authorizationHeader, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdateForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(invalidPasswordError));
    }

    @Test
    void updateUserPassword_error_invalidPassword() throws Exception{
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(newPassword)
            .match(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(newPassword));
        
        getMockMvc().perform(
            patch(url)
                .header(authorizationHeader, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordUpdateForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(invalidPasswordError));
    }

}
