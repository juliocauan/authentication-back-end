package br.com.juliocauan.authentication.security.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.security.service.UserDetailsServiceImpl;

public class UserDetailsServiceTest extends TestContext {

    private final UserDetailsServiceImpl userDetailsService;
    
    private final String password = "12345678";
    private final String username = "testUsername";
    private final String email = "test@email.com";
    private final String errorUsernameNotFound = "User Not Found with username: " + username;

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();

    public UserDetailsServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, UserDetailsServiceImpl userDetailsService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.userDetailsService = userDetailsService;
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
    public void givenPresentUsername_WhenLoadUserByUsername_ThenUserDetails(){
        getUserRepository().save(entity);
        UserDetails user = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, user.getUsername());
        Assertions.assertEquals(password, user.getPassword());
        Assertions.assertEquals(roles.size(), user.getAuthorities().size());
    }
    
    @Test
    public void givenNotPresentUsername_WhenLoadUserByUsername_ThenUsernameNotFoundException(){
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username), errorUsernameNotFound);
    }

}
