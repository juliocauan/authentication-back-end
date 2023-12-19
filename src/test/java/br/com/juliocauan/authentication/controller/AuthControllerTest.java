package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EmailPasswordResetUrlRequest;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetEntity;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class AuthControllerTest extends TestContext {

    private final PasswordEncoder encoder;
    private final PasswordResetRepositoryImpl passwordResetTokenRepository;

    private final String urlSignup = "/signup";
    private final String urlSignin = "/login";
    private final String urlForgotPassword = "/forgot-password";
    private final String urlForgotPasswordWithToken = "/forgot-password/{token}";

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();
    private final String newPassword = getRandomPassword();
    private final String tokenMock = getRandomToken();

    private final String okMessage = "User registered successfully!";
    private final String okEmailPasswordReset = "Email sent to [" + username + "] successfully!";
    private final String okResetUserPassword = "Password updated successfully!";
    
    private final String invalidPasswordError = "Passwords don't match!";
    private final String errorTokenExpired = "Expired Token!";
    private final String errorValidation = "Input validation error!";
    private final String errorBadCredentials = "Bad credentials";

    private final SigninForm signinForm = new SigninForm();
    private final SignupForm signupForm = new SignupForm();
    private final PasswordMatch passwordMatch = new PasswordMatch();

    public AuthControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordEncoder encoder, PasswordResetRepositoryImpl passwordResetTokenRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.encoder = encoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public void beforeAll() {
        super.beforeAll();
        getRoleRepository().save(RoleEntity.builder().name("USER").build());
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        signupForm.username(username).match(new PasswordMatch().password(password).passwordConfirmation(password));
        signinForm.username(username).password(password);
        passwordMatch.password(newPassword).passwordConfirmation(newPassword);
    }

    private final UserEntity saveUser() {
        return getUserRepository().save(UserEntity.builder()
            .password(encoder.encode(password))
            .username(username)
            .roles(null)
        .build());
    }

    private final void savePasswordReset() {
        passwordResetTokenRepository.save(PasswordResetEntity
            .builder()
                .token(tokenMock)
                .user(saveUser())
            .build());
    }

    @Test
    void signup() throws Exception{
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(signupForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value(okMessage));
    }

    @Test
    void signup_error_invalidSignupForm() throws Exception{
        signupForm.username("aaaaaaaaaaaaa").match(new PasswordMatch().password("12345").passwordConfirmation("12345"));
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(signupForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorValidation))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors", hasSize(3)));
    }

    @Test
    void signup_error_duplicatedUsername() throws Exception{
        saveUser();
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(signupForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(getErrorUsernameDuplicated(username)))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void signin() throws Exception{
        saveUser();
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void signin_error_invalidSigninForm() throws Exception{
        signinForm.username("aaaaaaaaaaaaa").password("12345");
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorValidation))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors", hasSize(2)));
    }

    @Test
    void signin_error_badCredentials() throws Exception{
        getMockMvc().perform(
            post(urlSignin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(signinForm)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorBadCredentials))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void emailPasswordReset() throws Exception {
        saveUser();
        EmailPasswordResetUrlRequest requestBody = new EmailPasswordResetUrlRequest().username(username);
        getMockMvc().perform(
            post(urlForgotPassword)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(requestBody)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(okEmailPasswordReset));
    }

    @Test
    void emailPasswordReset_error_userNotFound() throws Exception {
        EmailPasswordResetUrlRequest requestBody = new EmailPasswordResetUrlRequest().username(username);
        getMockMvc().perform(
            post(urlForgotPassword)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(requestBody)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(getErrorUsernameNotFound(username)))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword() throws Exception {
        savePasswordReset();
        getMockMvc().perform(
            patch(urlForgotPasswordWithToken, tokenMock)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(passwordMatch)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(okResetUserPassword));
    }

    @Test
    void resetUserPassword_error_passwordMatch() throws Exception {
        passwordMatch.password(password);
        getMockMvc().perform(
            patch(urlForgotPasswordWithToken, tokenMock)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(passwordMatch)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(invalidPasswordError))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword_error_entityNotFound() throws Exception {
        getMockMvc().perform(
            patch(urlForgotPasswordWithToken, tokenMock)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(passwordMatch)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(getErrorPasswordResetNotFound(tokenMock)))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword_error_tokenExpired() throws Exception {
        passwordResetTokenRepository.save(PasswordResetEntity
            .builder()
                .token(tokenMock)
                .user(saveUser())
                .expireDate(LocalDateTime.now().minusSeconds(1))
            .build());
        getMockMvc().perform(
            patch(urlForgotPasswordWithToken, tokenMock)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(passwordMatch)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorTokenExpired))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

}