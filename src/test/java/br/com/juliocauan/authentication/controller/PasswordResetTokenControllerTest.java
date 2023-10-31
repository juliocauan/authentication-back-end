package br.com.juliocauan.authentication.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class PasswordResetTokenControllerTest extends TestContext {

    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;

    private final String urlForgotPassword = "/api/auth/forgotpassword";
    private final String urlForgotPasswordWithToken = "/api/auth/forgotpassword/{token}";

    private final String username = "test@email.com";
    private final String password = "1234567890";
    private final String newPassword = "0987654321";
    private final String tokenMock = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    private final String okEmailPasswordResetToken = "Email sent to " + username + " successfully!";
    private final String okResetUserPassword = "Password updated successfully!";
    private final String errorUsernameNotFound = "User Not Found with username: " + username;
    private final String errorPasswordMatch = "Confirmation and new password are different!";
    private final String errorTokenNotFound = "Password Reset Token not found with token: " + tokenMock;
    private final String errorTokenExpired = "Expired Password Reset Token!";

    private final PasswordMatch passwordMatch = new PasswordMatch();

    public PasswordResetTokenControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetTokenRepositoryImpl passwordResetTokenRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @BeforeEach
    void standard() {
        getUserRepository().deleteAll();
        passwordMatch.password(newPassword).passwordConfirmation(newPassword);
    }

    private final void buildAndSavePasswordResetToken() {
        passwordResetTokenRepository.save(PasswordResetTokenEntity
            .builder()
                .token(tokenMock)
                .user(buildAndSaveUser())
            .build());
    }

    private final UserEntity buildAndSaveUser() {
        return getUserRepository().save(UserEntity
            .builder()
                .id(null)
                .username(username)
                .password(password)
                .roles(new HashSet<>())
            .build());
    }

    @Test
    void emailPasswordResetToken() throws Exception {
        buildAndSaveUser();
        getMockMvc().perform(
            post(urlForgotPassword)
                .contentType(MediaType.APPLICATION_JSON)
                .content(username))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body").value(okEmailPasswordResetToken));
    }

    @Test
    void emailPasswordResetToken_error_userNotFound() throws Exception {
        getMockMvc().perform(
            post(urlForgotPassword)
                .contentType(MediaType.APPLICATION_JSON)
                .content(username))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(errorUsernameNotFound))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword() throws Exception {
        buildAndSavePasswordResetToken();
        getMockMvc().perform(
            patch(urlForgotPasswordWithToken, tokenMock)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordMatch)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body").value(okResetUserPassword));
    }

    @Test
    void resetUserPassword_error_passwordMatch() throws Exception {
        passwordMatch.password(password);
        getMockMvc().perform(
            patch(urlForgotPasswordWithToken, tokenMock)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordMatch)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorPasswordMatch))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword_error_entityNotFound() throws Exception {
        getMockMvc().perform(
            patch(urlForgotPasswordWithToken, tokenMock)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordMatch)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(errorTokenNotFound))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword_error_tokenExpired() throws Exception {
        passwordResetTokenRepository.save(PasswordResetTokenEntity
            .builder()
                .token(tokenMock)
                .user(buildAndSaveUser())
                .expireDate(LocalDateTime.now().minusSeconds(1))
            .build());
        getMockMvc().perform(
            patch(urlForgotPasswordWithToken, tokenMock)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(passwordMatch)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorTokenExpired))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

}
