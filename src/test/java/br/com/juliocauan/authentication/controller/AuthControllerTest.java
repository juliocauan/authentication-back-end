package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EmailPasswordResetRequest;
import org.openapitools.model.EmailType;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.SignupFormAdmin;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetEntity;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.util.EmailUtil;

class AuthControllerTest extends TestContext {

    private final PasswordEncoder encoder;
    private final PasswordResetRepositoryImpl passwordResetRepository;

    private final String urlLogin = "/login";
    private final String urlSignup = "/signup";
    private final String urlSignupAdmin = "/signup/admin";
    private final String urlForgotPassword = "/forgot-password";
    private final String urlForgotPasswordWithToken = "/forgot-password/{token}";

    private final String rawPassword = getRandomPassword();
    private final String adminKey = "@Admin123";

    public AuthControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordEncoder encoder,
            PasswordResetRepositoryImpl passwordResetRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.encoder = encoder;
        this.passwordResetRepository = passwordResetRepository;
        EmailUtil.setEmailer("admin@authentication.test", "admin", EmailType.GREEN_MAIL);
    }

    @BeforeEach
    void beforeEach() {
        getUserRepository().deleteAll();
    }

    private final UserEntity saveUser() {
        return getUserRepository().save(UserEntity
                .builder()
                .username(getRandomUsername())
                .password(encoder.encode(rawPassword))
                .build());
    }

    private final void savePasswordReset(String token) {
        passwordResetRepository.save(PasswordResetEntity
                .builder()
                .token(token)
                .user(saveUser())
                .build());
    }

    @Test
    void login() throws Exception {
        User user = saveUser();
        SigninForm signinForm = new SigninForm(user.getUsername(), rawPassword);
        getMockMvc().perform(
                post(urlLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signinForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isEmpty())
                .andExpect(jsonPath("$.JWT").isNotEmpty());
    }

    @Test
    void login_branch_withRoles() throws Exception {
        RoleEntity role = getRoleRepository().save(new RoleEntity("TEST"));
        User user = getUserRepository().save(UserEntity.builder()
                .username(getRandomUsername())
                .password(encoder.encode(rawPassword))
                .roles(Collections.singleton(role))        
        .build());
        SigninForm signinForm = new SigninForm(user.getUsername(), rawPassword);
        getMockMvc().perform(
                post(urlLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signinForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("TEST"))
                .andExpect(jsonPath("$.JWT").isNotEmpty());
    }

    @Test
    void login_error_invalidSigninForm() throws Exception {
        String invalidInput = getRandomString(5);
        SigninForm signinForm = new SigninForm(invalidInput, invalidInput);
        getMockMvc().perform(
                post(urlLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signinForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Input validation error!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)));
    }

    @Test
    void login_error_badCredentials() throws Exception {
        SigninForm signinForm = new SigninForm(getRandomUsername(), getRandomPassword());
        getMockMvc().perform(
                post(urlLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signinForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad credentials"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void signup() throws Exception {
        String username = getRandomUsername();
        SignupForm signupForm = new SignupForm(username, new PasswordMatch(rawPassword, rawPassword));
        getMockMvc().perform(
                post(urlSignup)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupForm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User [%s] registered successfully!".formatted(username)));
    }

    @Test
    void signup_error_invalidSignupForm() throws Exception {
        String invalidInput = getRandomString(5);
        SignupForm signupForm = new SignupForm(invalidInput, new PasswordMatch(invalidInput, invalidInput));
        getMockMvc().perform(
                post(urlSignup)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Input validation error!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors", hasSize(3)));
    }

    @Test
    void signup_error_invalidPasswordMatch() throws Exception {
        SignupForm signupForm = new SignupForm(getRandomUsername(),
                new PasswordMatch(getRandomPassword(), getRandomPassword()));
        getMockMvc().perform(
                post(urlSignup)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords don't match!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void signup_error_duplicatedUsername() throws Exception {
        String username = saveUser().getUsername();
        SignupForm signupForm = new SignupForm(username, new PasswordMatch(rawPassword, rawPassword));
        getMockMvc().perform(
                post(urlSignup)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(getErrorUsernameDuplicated(username)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void signupAdmin() throws Exception {
        String username = getRandomUsername();
        SignupFormAdmin signupFormAdmin = new SignupFormAdmin(username, new PasswordMatch(rawPassword, rawPassword),
                adminKey);
        getMockMvc().perform(
                post(urlSignupAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupFormAdmin)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Admin [%s] registered successfully!".formatted(username)));
    }

    @Test
    void signupAdmin_error_invalidSignupForm() throws Exception {
        String invalidInput = getRandomString(5);
        SignupFormAdmin signupFormAdmin = new SignupFormAdmin(invalidInput,
                new PasswordMatch(invalidInput, invalidInput), adminKey);
        getMockMvc().perform(
                post(urlSignupAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupFormAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Input validation error!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors", hasSize(3)));
    }

    @Test
    void signupAdmin_error_invalidPasswordMatch() throws Exception {
        SignupFormAdmin signupFormAdmin = new SignupFormAdmin(getRandomUsername(),
                new PasswordMatch(getRandomPassword(), getRandomPassword()), adminKey);
        getMockMvc().perform(
                post(urlSignupAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupFormAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords don't match!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void signupAdmin_error_adminKey() throws Exception {
        SignupFormAdmin signupFormAdmin = new SignupFormAdmin(getRandomUsername(),
                new PasswordMatch(rawPassword, rawPassword), getRandomString(10));
        getMockMvc().perform(
                post(urlSignupAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupFormAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Admin Key is incorrect!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void signupAdmin_error_duplicatedUsername() throws Exception {
        String username = saveUser().getUsername();
        SignupFormAdmin signupFormAdmin = new SignupFormAdmin(username, new PasswordMatch(rawPassword, rawPassword),
                adminKey);
        getMockMvc().perform(
                post(urlSignupAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupFormAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(getErrorUsernameDuplicated(username)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void emailPasswordReset() throws Exception {
        String username = saveUser().getUsername();
        EmailPasswordResetRequest requestBody = new EmailPasswordResetRequest().username(username);
        getMockMvc().perform(
                post(urlForgotPassword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent to [%s] successfully!".formatted(username)));
    }

    @Test
    void emailPasswordReset_error_invalidInput() throws Exception {
        EmailPasswordResetRequest requestBody = new EmailPasswordResetRequest().username(getRandomString(5));
        getMockMvc().perform(
                post(urlForgotPassword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Input validation error!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
    }

    @Test
    void emailPasswordReset_error_userNotFound() throws Exception {
        String username = getRandomUsername();
        EmailPasswordResetRequest requestBody = new EmailPasswordResetRequest().username(username);
        getMockMvc().perform(
                post(urlForgotPassword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(getErrorUsernameNotFound(username)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword() throws Exception {
        String token = getRandomToken();
        PasswordMatch passwordMatch = new PasswordMatch(rawPassword, rawPassword);
        savePasswordReset(token);
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully!"));
    }

    @Test
    void resetUserPassword_error_invalidInput() throws Exception {
        String invalidInput = getRandomString(5);
        PasswordMatch passwordMatch = new PasswordMatch(invalidInput, invalidInput);
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, invalidInput)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Input validation error!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)));
    }

    @Test
    void resetUserPassword_error_passwordMatch() throws Exception {
        String token = getRandomToken();
        PasswordMatch passwordMatch = new PasswordMatch(getRandomPassword(), getRandomPassword());
        savePasswordReset(token);
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords don't match!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword_error_tokenNotFound() throws Exception {
        String token = getRandomToken();
        PasswordMatch passwordMatch = new PasswordMatch(rawPassword, rawPassword);
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(getErrorPasswordResetNotFound(token)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetUserPassword_error_tokenExpired() throws Exception {
        String token = getRandomToken();
        PasswordMatch passwordMatch = new PasswordMatch(rawPassword, rawPassword);
        passwordResetRepository.save(PasswordResetEntity
                .builder()
                .token(token)
                .user(saveUser())
                .expireDate(LocalDateTime.now().minusSeconds(1))
                .build());
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Expired Token!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

}