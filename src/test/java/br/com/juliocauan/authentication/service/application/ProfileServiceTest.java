package br.com.juliocauan.authentication.service.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.PasswordUpdateForm;
import org.openapitools.model.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.service.util.PasswordService;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.ProfileServiceImpl;

class ProfileServiceTest extends TestContext {

    private final ProfileServiceImpl profileService;
    private final AuthenticationManager authenticationManager;
    private final PasswordService passwordService;

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();
    private final String newPassword = getRandomPassword();
    private final String invalidPasswordError = "Passwords don't match!";

    private UserEntity userEntity;

    public ProfileServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, ProfileServiceImpl profileService,
            AuthenticationManager authenticationManager, PasswordService passwordService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.profileService = profileService;
        this.authenticationManager = authenticationManager;
        this.passwordService = passwordService;
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        userEntity = getUserRepository().save(UserEntity
            .builder()
                .username(username)
                .password(passwordService.encode(password))
                .roles(null)
            .build());
        deauthenticate();
    }

    private final void authenticate() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private final void deauthenticate() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void getProfileContent() {
        Profile expectedProfile = new Profile().username(username);

        authenticate();
        assertEquals(expectedProfile, profileService.getProfileContent());
    }

    @Test
    void getProfileContent_error() {
        deauthenticate();
        assertThrowsExactly(NullPointerException.class, () -> profileService.getProfileContent());
    }

    @Test
    void updatePassword() {
        authenticate();
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(password)
            .newPasswordMatch(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(newPassword));
        
        profileService.updatePassword(passwordUpdateForm);
        UserEntity userWithAlteredPassword = getUserRepository().findById(userEntity.getId()).get();
        assertNotEquals(userEntity.getPassword(), userWithAlteredPassword.getPassword());
        assertEquals(userEntity.getPassword().length(), userWithAlteredPassword.getPassword().length());
    }

    @Test
    void updatePassword_error_passwordMatch() {
        authenticate();
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(password)
            .newPasswordMatch(new PasswordMatch()
                .password(password)
                .passwordConfirmation(newPassword));
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> profileService.updatePassword(passwordUpdateForm));
        assertEquals(invalidPasswordError, exception.getMessage());

        deauthenticate();
        assertThrowsExactly(NullPointerException.class, () -> profileService.updatePassword(passwordUpdateForm));
    }

    @Test
    void updatePassword_error_invalidPassword() {
        authenticate();
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .currentPassword(newPassword)
            .newPasswordMatch(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(newPassword));
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> profileService.updatePassword(passwordUpdateForm));
        assertEquals(invalidPasswordError, exception.getMessage());
    }

    @Test
    void updatePassword_error() {
        deauthenticate();
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm();
        assertThrowsExactly(NullPointerException.class, () -> profileService.updatePassword(passwordUpdateForm));
    }
    
}
