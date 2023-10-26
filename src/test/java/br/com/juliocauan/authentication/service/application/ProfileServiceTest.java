package br.com.juliocauan.authentication.service.application;

import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
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
import br.com.juliocauan.authentication.infrastructure.exception.InvalidOldPasswordException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordConfirmationException;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.ProfileServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.PasswordService;

class ProfileServiceTest extends TestContext {

    private final ProfileServiceImpl profileService;
    private final AuthenticationManager authenticationManager;
    private final PasswordService passwordService;

    private final String username = "test@email.com";
    private final String password = "123456789";
    private final String newPassword = "0987654321";
    private final String currentPasswordError = "Wrong current password!";
    private final String confirmationPasswordError = "Confirmation and new password are different!";

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
    public void standard(){
        getUserRepository().deleteAll();
        userEntity = getUserRepository().save(UserEntity
            .builder()
                .username(username)
                .password(passwordService.encodePassword(password))
                .roles(new HashSet<>())
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
        Assertions.assertEquals(expectedProfile, profileService.getProfileContent());
    }

    @Test
    void getProfileContent_Errors() {
        deauthenticate();
        Assertions.assertThrowsExactly(NullPointerException.class, () -> profileService.getProfileContent());
    }

    @Test
    void alterPassword() {
        authenticate();
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .oldPassword(password)
            .newPasswordMatch(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(newPassword));
        
        profileService.alterPassword(passwordUpdateForm);
        UserEntity userWithAlteredPassword = getUserRepository().findById(userEntity.getId()).get();
        Assertions.assertNotEquals(userEntity.getPassword(), userWithAlteredPassword.getPassword());
        Assertions.assertEquals(userEntity.getPassword().length(), userWithAlteredPassword.getPassword().length());
    }

    @Test
    void alterPassword_Errors() {
        authenticate();
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
            .oldPassword(password)
            .newPasswordMatch(new PasswordMatch()
                .password(password)
                .passwordConfirmation(newPassword));
        PasswordConfirmationException confirmationException = Assertions.assertThrowsExactly(PasswordConfirmationException.class,
            () -> profileService.alterPassword(passwordUpdateForm));
        Assertions.assertEquals(confirmationPasswordError, confirmationException.getMessage());

        passwordUpdateForm
            .oldPassword(newPassword)
            .newPasswordMatch(new PasswordMatch()
                .password(newPassword)
                .passwordConfirmation(newPassword));
        InvalidOldPasswordException currentPasswordException = Assertions.assertThrowsExactly(InvalidOldPasswordException.class,
            () -> profileService.alterPassword(passwordUpdateForm));
        Assertions.assertEquals(currentPasswordError, currentPasswordException.getMessage());

        deauthenticate();
        Assertions.assertThrowsExactly(NullPointerException.class, () -> profileService.alterPassword(passwordUpdateForm));
    }
    
}
