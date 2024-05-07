package br.com.juliocauan.authentication.service.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.application.ProfileService;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;

class ProfileServiceTest extends TestContext {

    private final ProfileService profileService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final String rawPassword = getRandomPassword();

    public ProfileServiceTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, ProfileService profileService,
            AuthenticationManager authenticationManager, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.profileService = profileService;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    @BeforeEach
    void beforeEach() {
        getUserRepository().deleteAll();
        deauthenticate();
    }

    private final void authenticate() {
        User user = saveUser();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(),
                rawPassword);
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private final void deauthenticate() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private final User saveUser() {
        return getUserRepository().save(new User(getRandomUsername(), encoder.encode(rawPassword)));
    }

    @Test
    void updatePassword() {
        String newPassword = getRandomPassword();
        authenticate();
        profileService.updatePassword(rawPassword, newPassword);

        User userAfter = getUserRepository().findAll().get(0);
        assertTrue(encoder.matches(newPassword, userAfter.getPassword()));
    }

    @Test
    void updatePassword_error_invalidPassword() {
        String incorrectCurrentPassword = getRandomPassword();
        String newPassword = getRandomPassword();
        authenticate();
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
                () -> profileService.updatePassword(incorrectCurrentPassword, newPassword));
        assertEquals("Passwords don't match!", exception.getMessage());
    }

    @Test
    void updatePassword_error_passwordSecurity() {
        String newPassword = "1234567yttis";
        authenticate();
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
                () -> profileService.updatePassword(rawPassword, newPassword));
        assertEquals("Password is not strong!", exception.getMessage());
    }

    @Test
    void updatePassword_error_notAuthenticated() {
        deauthenticate();
        assertThrowsExactly(NullPointerException.class,
                () -> profileService.updatePassword(getRandomPassword(), getRandomPassword()));
    }

    @Test
    void closeAccount() {
        authenticate();
        assertFalse(getUserRepository().findAll().isEmpty());
        profileService.closeAccount(rawPassword);
        assertTrue(getUserRepository().findAll().isEmpty());
    }

    @Test
    void closeAccount_error_invalidPassword() {
        authenticate();
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
                () -> profileService.closeAccount(getRandomPassword()));
        assertEquals("Passwords don't match!", exception.getMessage());
    }
    
    @Test
    void closeAccount_error_notAuthenticated() {
        deauthenticate();
        assertThrowsExactly(NullPointerException.class,
                () -> profileService.closeAccount(rawPassword));
    }

}
