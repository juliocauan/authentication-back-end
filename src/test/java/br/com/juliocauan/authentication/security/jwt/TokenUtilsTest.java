package br.com.juliocauan.authentication.security.jwt;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.security.jwt.TokenUtils;

public class TokenUtilsTest extends TestContext{

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    
    private final String password = "12345678";
    private final String username = "testUsername";
    private final String email = "test@email.com";

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();

    public TokenUtilsTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, TokenUtils tokenUtils, AuthenticationManager authenticationManager) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
    }

    @Override @BeforeAll
    public void setup(){
        super.setup();
        getRoleRepository().findAll().forEach(role -> roles.add(role));
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        entity = UserEntity.builder()
            .email(email)
            .password(password)
            .username(username)
            .roles(roles)
        .build();
    }

    @Test
    public void generateTokenTest(){
        getUserRepository().save(entity);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
        // String token = tokenUtils.generateToken(auth);
        Assertions.assertDoesNotThrow(() -> tokenUtils.generateToken(auth));
    }
    
}
