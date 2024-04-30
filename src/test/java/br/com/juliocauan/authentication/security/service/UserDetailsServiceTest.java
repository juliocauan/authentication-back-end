package br.com.juliocauan.authentication.security.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.security.service.UserDetailsServiceImpl;

class UserDetailsServiceTest extends TestContext {

    private final UserDetailsServiceImpl userDetailsService;
    
    private final String username = getRandomUsername();
    private final String password = getRandomPassword();

    private User entity;
    private Set<Role> roles = new HashSet<>();

    public UserDetailsServiceTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, UserDetailsServiceImpl userDetailsService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.userDetailsService = userDetailsService;
    }

    @Override @BeforeAll
    public void beforeAll(){
        super.beforeAll();
        getRoleRepository().findAll().forEach(role -> roles.add(role));
    }

    @BeforeEach
    void beforeEach(){
        getUserRepository().deleteAll();
        entity = new User(username, password);
        entity.setRoles(roles);
    }
    
    @Test
    void loadByUsername(){
        getUserRepository().save(entity);
        UserDetails user = userDetailsService.loadUserByUsername(username);
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(roles.size(), user.getAuthorities().size());
    }
    
    @Test
    void loadByUsername_error_usernameNotFound(){
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(username));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

}
