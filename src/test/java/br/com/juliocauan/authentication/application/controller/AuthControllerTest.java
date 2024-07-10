package br.com.juliocauan.authentication.application.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.SendResetTokenRequest;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.SignupFormAdmin;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.EmailUtil;

class AuthControllerTest extends TestContext {

    private final PasswordEncoder encoder;
    private final PasswordResetRepository passwordResetRepository;

    private final String urlLogin = "/login";
    private final String urlSignup = "/signup";
    private final String urlSignupAdmin = "/signup/admin";
    private final String urlForgotPassword = "/forgot-password";
    private final String urlForgotPasswordWithToken = "/forgot-password/{token}";

    private final String rawPassword = getRandomPassword();
    private final String adminKey = "@Admin123";

    public AuthControllerTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordEncoder encoder,
            PasswordResetRepository passwordResetRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.encoder = encoder;
        this.passwordResetRepository = passwordResetRepository;
    }

    @BeforeEach
    void beforeEach() {
        getUserRepository().deleteAll();
    }

    private final User saveUser() {
        return getUserRepository().save(new User(getRandomUsername(), encoder.encode(rawPassword)));
    }

    private final PasswordReset savePasswordReset() {
        return passwordResetRepository.save(new PasswordReset(saveUser()));
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
        Role role = getRoleRepository().save(new Role("TEST"));
        User user = new User(getRandomUsername(), encoder.encode(rawPassword));
        user.setRoles(Collections.singleton(role));
        getUserRepository().save(user);
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
    void login_error_disabled() throws Exception {
        User user = new User(getRandomUsername(), encoder.encode(rawPassword));
        user.setDisabled(true);
        getUserRepository().save(user);
        SigninForm signinForm = new SigninForm(user.getUsername(), rawPassword);
        getMockMvc().perform(
                post(urlLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signinForm)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User is disabled"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void login_error_locked() throws Exception {
        User user = new User(getRandomUsername(), encoder.encode(rawPassword));
        user.setLocked(true);
        getUserRepository().save(user);
        SigninForm signinForm = new SigninForm(user.getUsername(), rawPassword);
        getMockMvc().perform(
                post(urlLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signinForm)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User account is locked"))
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
                .andExpect(jsonPath("$.message").value("Username [%s] is already taken!".formatted(username)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void signup_error_weakPassword() throws Exception {
        String weakPassword = "12345678";
        SignupForm signupForm = new SignupForm(getRandomUsername(),
                new PasswordMatch(weakPassword, weakPassword));
        getMockMvc().perform(
                post(urlSignup)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password is not strong!"))
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
    void signupAdmin_error_weakPassword() throws Exception {
        String weakPassword = "12345678";
        SignupFormAdmin signupFormAdmin = new SignupFormAdmin(getRandomUsername(),
                new PasswordMatch(weakPassword, weakPassword), adminKey);
        getMockMvc().perform(
                post(urlSignupAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(signupFormAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password is not strong!"))
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
                .andExpect(jsonPath("$.message").value("Username [%s] is already taken!".formatted(username)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void sendResetToken() throws Exception {
        String username = saveUser().getUsername();
        SendResetTokenRequest requestBody = new SendResetTokenRequest().username(username);
        getMockMvc().perform(
                post(urlForgotPassword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent to [%s]!".formatted(username)));
    }

    @Test
    void sendResetToken_error_invalidInput() throws Exception {
        SendResetTokenRequest requestBody = new SendResetTokenRequest().username(getRandomString(5));
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
    void sendResetToken_error_userNotFound() throws Exception {
        String username = getRandomUsername();
        SendResetTokenRequest requestBody = new SendResetTokenRequest().username(username);
        getMockMvc().perform(
                post(urlForgotPassword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Username [%s] not found!".formatted(username)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void sendResetToken_error_emailerNotSet() throws Exception {
        EmailUtil.setEmailer(getRandomUsername(), getRandomPassword(), null);
        String username = saveUser().getUsername();
        SendResetTokenRequest requestBody = new SendResetTokenRequest().username(username);
        getMockMvc().perform(
                post(urlForgotPassword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(requestBody)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Emailer not set. ADMIN must set one."));
    }

    @Test
    void resetPassword() throws Exception {
        PasswordMatch passwordMatch = new PasswordMatch(rawPassword, rawPassword);
        String token = savePasswordReset().getToken();
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully!"));
    }

    @Test
    void resetPassword_error_invalidInput() throws Exception {
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
    void resetPassword_error_passwordMatch() throws Exception {
        PasswordMatch passwordMatch = new PasswordMatch(getRandomPassword(), getRandomPassword());
        String token = savePasswordReset().getToken();
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
    void resetPassword_error_weakPassword() throws Exception {
        String weakPassword = "12345678";
        PasswordMatch passwordMatch = new PasswordMatch(weakPassword, weakPassword);
        String token = savePasswordReset().getToken();
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password is not strong!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetPassword_error_tokenNotFound() throws Exception {
        String token = getRandomToken();
        PasswordMatch passwordMatch = new PasswordMatch(rawPassword, rawPassword);
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Token [%s] not found!".formatted(token)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void resetPassword_error_tokenExpired() throws Exception {
        PasswordMatch passwordMatch = new PasswordMatch(rawPassword, rawPassword);
        PasswordReset passwordReset = new PasswordReset(saveUser());
        passwordReset.setExpireDate(LocalDateTime.now().minusSeconds(1));
        passwordResetRepository.save(passwordReset);
        getMockMvc().perform(
                patch(urlForgotPasswordWithToken, passwordReset.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(passwordMatch)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Expired Token!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

}