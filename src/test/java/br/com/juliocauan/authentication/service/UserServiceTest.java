package br.com.juliocauan.authentication.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import jakarta.persistence.EntityExistsException;

class UserServiceTest extends TestContext {

    private final UserServiceImpl userService;

    private final String password = "12345678";
    private final String username = "test@email.com";

    private final String errorUsernameNotFound =  "User Not Found with username: " + username;
    private final String errorDuplicatedUsername = "Username is already taken!";

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();

    public UserServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, UserServiceImpl userService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.userService = userService;
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
            .password(password)
            .username(username)
            .roles(roles)
        .build();
    }

    @Test
    void whenGetRepository_ThenInstanceOfUserRepository(){
        Assertions.assertInstanceOf(UserRepository.class, userService.getRepository());
    }

    @Test
    void givenPresentUsername_WhenGetByUsername_ThenEqualsUser(){
        getUserRepository().save(entity);
        Assertions.assertEquals(entity, userService.getByUsername(username));
    }

    @Test
    void givenNotPresentUsername_WhenGetByUsername_ThenUsernameNotFoundException(){
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername(username), errorUsernameNotFound);
    }

    @Test
    void givenDuplicatedUsername_WhenCheckDuplicatedUsername_ThenEntityExistsException(){
        getUserRepository().save(entity);
        Assertions.assertThrows(EntityExistsException.class, () -> userService.checkDuplicatedUsername(username), errorDuplicatedUsername);
    }

    @Test
    void givenNotDuplicatedUsername_WhenCheckDuplicatedUsername_ThenVoid(){
        Assertions.assertDoesNotThrow(() -> userService.checkDuplicatedUsername(username));
    }

    @Test
    void givenValidUserEntity_WhenSave_ThenVoid(){
        Assertions.assertDoesNotThrow(() -> userService.save(entity));
    }
    
}
