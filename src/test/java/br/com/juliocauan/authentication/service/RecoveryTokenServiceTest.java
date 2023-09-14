package br.com.juliocauan.authentication.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.RecoveryTokenServiceImpl;

class RecoveryTokenServiceTest extends TestContext {

    private final RecoveryTokenServiceImpl recoveryTokenService;

    private final String invalidUsername = "notPresent@email.test";
    private final String password = "12345678";
    //TODO review this email
    private final String username = "jcam.test01@gmail.com";

    private final String usernameNotFoundException = "User Not Found with username: " + invalidUsername;

    private UserEntity user;
    private Set<RoleEntity> roles = new HashSet<>();

    public RecoveryTokenServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, RecoveryTokenServiceImpl recoveryTokenService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.recoveryTokenService = recoveryTokenService;
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        user = UserEntity.builder()
            .password(password)
            .username(username)
            .roles(roles)
        .build();
    }

    @Test
    void givenNotPresentUsername_WhenGenerateLinkAndSendEmail_ThenUsernameNotFoundException() {
        UsernameNotFoundException exception = Assertions.assertThrowsExactly(
            UsernameNotFoundException.class,
            () -> recoveryTokenService.generateLinkAndSendEmail(invalidUsername));

        //TODO review tests that compares message errors
        Assertions.assertTrue(exception.getMessage().contentEquals(usernameNotFoundException));
    }

    @Test
    void givenUsername_WhenGenerateLinkAndSendEmail_ThenVoid() {
        getUserRepository().save(user);
        Assertions.assertDoesNotThrow(() -> recoveryTokenService.generateLinkAndSendEmail(user.getUsername()));
    }
    
}
