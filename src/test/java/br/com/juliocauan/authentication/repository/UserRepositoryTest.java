package br.com.juliocauan.authentication.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class UserRepositoryTest extends TestContext {

    private final String password = "12345678";
    private final String username = "test@email.com";
    private final String usernameContains = "test";
    private final String usernameNotContains = "asd";
    private final EnumRole rolePresent = EnumRole.MANAGER;
    private final EnumRole roleNotPresent = EnumRole.ADMIN;

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();
    
    public UserRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Override @BeforeAll
    public void setup(){
        super.setup();
        roles.add(new RoleEntity(getRoleRepository().getByName(rolePresent).get()));
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        entity = UserEntity.builder()
            .password(password)
            .username(username)
            .roles(roles)
        .build();
    }

    @Test
    void givenPresentUsername_WhenExistsByUsername_ThenTrue(){
        getUserRepository().save(entity);
        assertTrue(getUserRepository().existsByUsername(username));
    }

    @Test
    void givenNotPresentUsername_WhenExistsByUsername_ThenFalse(){
        assertFalse(getUserRepository().existsByUsername(username));
    }

    @Test
    void givenPresentUsername_WhenFindByUsername_ThenUser(){
        getUserRepository().save(entity);
        assertEquals(entity, getUserRepository().getByUsername(username).get());
    }

    @Test
    void givenNotPresentUsername_WhenFindByUsername_ThenUserNotPresent(){
        assertFalse(getUserRepository().getByUsername(username).isPresent());
    }

    @Test
    void givenUsernameContainsAndRole_WhenFindAllByUsernameContainsAndRole_ThenUserList() {
        getUserRepository().save(entity);
        List<User> userList = getUserRepository().getAllByUsernameSubstringAndRole(usernameContains, rolePresent);
        assertEquals(1, userList.size());
        assertEquals(entity, userList.get(0));
        
        entity = UserEntity.builder()
            .username(username + "2")
            .password(password)
            .roles(roles)
        .build();
        getUserRepository().save(entity);
        userList = getUserRepository().getAllByUsernameSubstringAndRole(usernameContains, rolePresent);
        assertEquals(2, userList.size());
        assertTrue(userList.contains(entity));

        userList = getUserRepository().getAllByUsernameSubstringAndRole(null, null);
        assertEquals(2, userList.size());

        userList = getUserRepository().getAllByUsernameSubstringAndRole(null, rolePresent);
        assertEquals(2, userList.size());

        userList = getUserRepository().getAllByUsernameSubstringAndRole(usernameContains, null);
        assertEquals(2, userList.size());
    }

    @Test
    void givenUsernameNotContainsAndRole_WhenFindAllByUsernameContainsAndRole_ThenEmptyUserList() {
        getUserRepository().save(entity);
        List<User> userList = getUserRepository().getAllByUsernameSubstringAndRole(usernameNotContains, rolePresent);
        assertTrue(userList.isEmpty());
    }

    @Test
    void givenUsernameContainsAndNotPresentRole_WhenFindAllByUsernameContainsAndRole_ThenEmptyUserList() {
        getUserRepository().save(entity);
        List<User> userList = getUserRepository().getAllByUsernameSubstringAndRole(usernameContains, roleNotPresent);
        assertTrue(userList.isEmpty());
    }

    @Test
    void givenUsernameNotContainsAndNotPresentRole_WhenFindAllByUsernameContainsAndRole_ThenEmptyUserList() {
        getUserRepository().save(entity);
        List<User> userList = getUserRepository().getAllByUsernameSubstringAndRole(usernameNotContains, roleNotPresent);
        assertTrue(userList.isEmpty());
    }

}
