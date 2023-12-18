package br.com.juliocauan.authentication.service.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.ProfileServiceImpl;
import br.com.juliocauan.authentication.util.PasswordUtil;

class ProfileServiceTest extends TestContext {

    private final ProfileServiceImpl profileService;
    private final AuthenticationManager authenticationManager;

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();
    private final String newPassword = getRandomPassword();
    private final String invalidPasswordError = "Passwords don't match!";

    private UserEntity userEntity;

    public ProfileServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, ProfileServiceImpl profileService,
            AuthenticationManager authenticationManager) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.profileService = profileService;
        this.authenticationManager = authenticationManager;
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        userEntity = getUserRepository().save(UserEntity
            .builder()
                .username(username)
                .password(PasswordUtil.encode(password))
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
    void updatePassword() {
        authenticate();
        profileService.updatePassword(password, newPassword);
        UserEntity userWithAlteredPassword = getUserRepository().findById(userEntity.getId()).get();
        assertNotEquals(userEntity.getPassword(), userWithAlteredPassword.getPassword());
        assertEquals(userEntity.getPassword().length(), userWithAlteredPassword.getPassword().length());
    }

    @Test
    void updatePassword_error_invalidPassword() {
        authenticate();
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
            () -> profileService.updatePassword(newPassword, newPassword));
        assertEquals(invalidPasswordError, exception.getMessage());
    }

    @Test
    void updatePassword_error() {
        deauthenticate();
        assertThrowsExactly(NullPointerException.class, () -> profileService.updatePassword(null, null));
    }
    
}
