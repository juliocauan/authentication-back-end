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

    private Set<RoleEntity> roles = new HashSet<>();
    
    public UserRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Override @BeforeAll
    public void beforeAll(){
        super.beforeAll();
        roles.add(getRoleRepository().findAll().get(0));
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
    }

    private final UserEntity getUser() {
        return UserEntity
            .builder()
                .username(getRandomUsername(null))
                .password(getRandomPassword())
                .roles(roles)
            .build();
    }

    private final User saveUser() {
        return getUserRepository().save(getUser());
    }

    private final String getFirstRole() {
        return roles.stream().findFirst().get().getName();
    }

    @Test
    void getByUsername(){
        User user = saveUser();
        assertEquals(user, getUserRepository().getByUsername(user.getUsername()).get());
    }

    @Test
    void getByUsername_notPresent(){
        getUserRepository().deleteAll();
        assertFalse(getUserRepository().getByUsername("USERNAME").isPresent());
    }

    @Test
    void getAll() {
        User expectedUser = saveUser();
        String roleName = getFirstRole();

        List<User> foundUsers = getUserRepository().getAll("@", roleName);

        assertEquals(1, foundUsers.size());
        assertEquals(expectedUser, foundUsers.get(0));
    }

    @Test
    void getAll_branch_usernameContainsAndRole() {
        User expectedUser = saveUser();
        saveUser();
        String roleName = getFirstRole();

        List<User> foundUsers = getUserRepository().getAll("@", roleName);

        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getAll_branch_usernameContainsAndNull() {
        User expectedUser = saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().getAll("@", null);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getAll_branch_nullAndRole() {
        User expectedUser = saveUser();
        saveUser();
        String roleName = getFirstRole();

        List<User> foundUsers = getUserRepository().getAll(null, roleName);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getAll_branch_nullAndNull() {
        User expectedUser = saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().getAll(null, null);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getAll_branch_usernameNotContainsAndRole() {
        List<User> foundUsers = getUserRepository().getAll("NOT_CONTAINS", getFirstRole());
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getAll_branch_usernameContainsAndRoleNotPresent() {
        List<User> foundUsers = getUserRepository().getAll("@", "NOT_A_ROLE");
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getAll_branch_usernameNotContainsAndRoleNotPresent() {
        List<User> foundUsers = getUserRepository().getAll("NOT_CONTAINS", "NOT_A_ROLE");
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void register() {
        UserEntity expectedUser = getUser();
        getUserRepository().register(expectedUser);
        UserEntity user = getUserRepository().findAll().get(0);
        assertEquals(expectedUser, user);
    }

    @Test
    void delete() {
        User user = saveUser();
        getUserRepository().delete(user);
        assertTrue(getUserRepository().findAll().isEmpty());
    }

}
