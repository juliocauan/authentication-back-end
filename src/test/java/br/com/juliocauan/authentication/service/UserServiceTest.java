package br.com.juliocauan.authentication.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
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
    private final String usernameContains = "test";
    private final String usernameNotContains = "asd";
    private final EnumRole rolePresent = EnumRole.MANAGER;
    private final EnumRole roleNotPresent = EnumRole.ADMIN;

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
        roles.add(new RoleEntity(getRoleRepository().findByName(rolePresent).get()));
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
    void givenValidUserEntity_WhenSave_ThenVoid(){
        Assertions.assertDoesNotThrow(() -> userService.save(entity));
    }

    @Test
    void givenPresentUsername_WhenGetByUsername_ThenEqualsUser(){
        getUserRepository().save(entity);
        Assertions.assertEquals(entity, userService.getByUsername(username));
    }

    @Test
    void givenNotPresentUsername_WhenGetByUsername_ThenUsernameNotFoundException(){
        UsernameNotFoundException exception = Assertions
            .assertThrowsExactly(UsernameNotFoundException.class, () -> userService.getByUsername(username));
        Assertions.assertEquals(errorUsernameNotFound, exception.getMessage());
    }

    @Test
    void givenDuplicatedUsername_WhenCheckDuplicatedUsername_ThenEntityExistsException(){
        getUserRepository().save(entity);
        EntityExistsException exception = Assertions
            .assertThrowsExactly(EntityExistsException.class, () -> userService.checkDuplicatedUsername(username));
        Assertions.assertEquals(errorDuplicatedUsername, exception.getMessage());
    }

    @Test
    void givenNotDuplicatedUsername_WhenCheckDuplicatedUsername_ThenVoid(){
        Assertions.assertDoesNotThrow(() -> userService.checkDuplicatedUsername(username));
    }

    @Test
    void getAllUsers(){
        getUserRepository().save(entity);
        List<User> userList = userService.getAllUsers(usernameContains, rolePresent);
        Assertions.assertEquals(1, userList.size());
        Assertions.assertEquals(entity, userList.get(0));
        
        entity = UserEntity.builder()
            .username(username + "2")
            .password(password)
            .roles(roles)
        .build();
        getUserRepository().save(entity);

        userList = userService.getAllUsers(usernameContains, rolePresent);
        Assertions.assertEquals(2, userList.size());
        Assertions.assertTrue(userList.contains(entity));

        userList = userService.getAllUsers(null, null);
        Assertions.assertEquals(2, userList.size());

        userList = userService.getAllUsers(null, rolePresent);
        Assertions.assertEquals(2, userList.size());

        userList = userService.getAllUsers(usernameContains, null);
        Assertions.assertEquals(2, userList.size());

        userList = userService.getAllUsers(usernameNotContains, null);
        Assertions.assertTrue(userList.isEmpty());

        userList = userService.getAllUsers(null, roleNotPresent);
        Assertions.assertTrue(userList.isEmpty());
    }
    
}
