package br.com.juliocauan.authentication.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class UserRepositoryTest extends TestContext {

    private final String username = getRandomUsername();
    private final String usernameContains = "test";
    private final String usernameNotContains = "asd";
    private final String password = getRandomPassword();
    private final String roleManager = "MANAGER";
    private final String roleAdmin = "ADMIN";

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();
    
    public UserRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Override @BeforeAll
    public void beforeAll(){
        super.beforeAll();
        getRoleRepository().save(RoleEntity.builder().name(roleManager).build());
        roles.add(new RoleEntity(getRoleRepository().getByName(roleManager).get()));
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        entity = getUserRepository().save(
            UserEntity.builder()
                .id(null)
                .username(username)
                .password(password)
                .roles(roles)
            .build());
    }

    private final void saveSecondUser() {
        getUserRepository().save(UserEntity
            .builder()
                .id(null)
                .username(username + "2")
                .password(password)
                .roles(roles)
            .build());
    }

    @Test
    void getByUsername(){
        assertEquals(entity, getUserRepository().getByUsername(username).get());
    }

    @Test
    void getByUsername_notPresent(){
        getUserRepository().deleteAll();
        assertFalse(getUserRepository().getByUsername(username).isPresent());
    }

    @Test
    void getAll() {
        UserEntity expectedUser = entity;
        List<User> foundUsers = getUserRepository().getAll(usernameContains, roleManager);
        assertEquals(1, foundUsers.size());
        assertEquals(expectedUser, foundUsers.get(0));
    }

    @Test
    void getAll_branch_usernameContainsAndRole() {
        saveSecondUser();
        List<User> foundUsers = getUserRepository().getAll(usernameContains, roleManager);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(entity));
    }

    @Test
    void getAll_branch_usernameContainsAndNull() {
        saveSecondUser();
        List<User> foundUsers = getUserRepository().getAll(usernameContains, null);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(entity));
    }

    @Test
    void getAll_branch_nullAndRole() {
        saveSecondUser();
        List<User> foundUsers = getUserRepository().getAll(null, roleManager);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(entity));
    }

    @Test
    void getAll_branch_nullAndNull() {
        saveSecondUser();
        List<User> foundUsers = getUserRepository().getAll(null, null);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(entity));
    }

    @Test
    void getAll_branch_usernameNotContainsAndRole() {
        List<User> foundUsers = getUserRepository().getAll(usernameNotContains, roleManager);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getAll_branch_usernameContainsAndRoleNotPresent() {
        List<User> foundUsers = getUserRepository().getAll(usernameContains, roleAdmin);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getAll_branch_usernameNotContainsAndRoleNotPresent() {
        List<User> foundUsers = getUserRepository().getAll(usernameNotContains, roleAdmin);
        assertTrue(foundUsers.isEmpty());
    }

}
