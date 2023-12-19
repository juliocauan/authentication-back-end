package br.com.juliocauan.authentication.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;

class UserServiceTest extends TestContext {

    private final UserServiceImpl userService;
    private final PasswordEncoder encoder;

    private final String username = getRandomUsername();
    private final String usernameContains = "test";
    private final String usernameNotContains = "@tset";
    private final String password = getRandomPassword();
    private final String roleManager = "MANAGER";
    private final String roleAdmin = "ADMIN";
    private final String roleUser = "USER";

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();

    public UserServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, UserServiceImpl userService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.userService = userService;
        this.encoder = encoder;
    }

    @Override @BeforeAll
    public void beforeAll(){
        super.beforeAll();
        getRoleRepository().save(RoleEntity.builder().name(roleManager).build());
        getRoleRepository().save(RoleEntity.builder().name(roleUser).build());
        roles.add(new RoleEntity(getRoleRepository().getByName(roleAdmin).get()));
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        entity = getUserRepository().save(
            UserEntity.builder()
                .password(encoder.encode(password))
                .username(username)
                .roles(roles)
            .build());
    }

    private final void saveSecondUser() {
        getUserRepository().save(UserEntity
            .builder()
                .id(null)
                .username(username + "2")
                .password(encoder.encode(password))
                .roles(roles)
            .build());
    }

    @Test
    void getByUsername(){
        assertEquals(entity, userService.getBy(username));
    }

    @Test
    void getByUsername_error_usernameNotFound(){
        getUserRepository().deleteAll();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
            () -> userService.getBy(username));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

    @Test
    void getUserInfos() {
        User expectedUserInfo = entity;
        List<User> foundUserInfos = userService.getUsers(usernameContains, roleAdmin);
        assertEquals(1, foundUserInfos.size());
        assertEquals(expectedUserInfo, foundUserInfos.get(0));
    }

    @Test
    void getUserInfosbranch_usernameContainsAndRole() {
        saveSecondUser();
        User expectedUserInfo = entity;
        List<User> foundUserInfos = userService.getUsers(usernameContains, roleAdmin);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfosbranch_usernameContainsAndNull() {
        saveSecondUser();
        User expectedUserInfo = entity;
        List<User> foundUserInfos = userService.getUsers(usernameContains, null);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfosbranch_nullAndRole() {
        saveSecondUser();
        User expectedUserInfo = entity;
        List<User> foundUserInfos = userService.getUsers(null, roleAdmin);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfosbranch_nullAndNull() {
        saveSecondUser();
        User expectedUserInfo = entity;
        List<User> foundUserInfos = userService.getUsers(null, null);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfosbranch_usernameNotContainsAndRole() {
        List<User> foundUserInfos = userService.getUsers(usernameNotContains, roleAdmin);
        assertTrue(foundUserInfos.isEmpty());
    }

    @Test
    void getUserInfosbranch_usernameContainsAndRoleNotPresent() {
        List<User> foundUserInfos = userService.getUsers(usernameContains, roleUser);
        assertTrue(foundUserInfos.isEmpty());
    }

    @Test
    void getUserInfosbranch_usernameNotContainsAndRoleNotPresent() {
        List<User> foundUserInfos = userService.getUsers(usernameNotContains, roleUser);
        assertTrue(foundUserInfos.isEmpty());
    }

    @Test
    void save(){
        getUserRepository().deleteAll();
        assertDoesNotThrow(() -> userService.registerNew(entity));
    }
    
    @Test
    void updatePassword() {
        User userBeforeUpdate = getUserRepository().save(entity);
        assertDoesNotThrow(() -> userService.updatePassword(userBeforeUpdate.getUsername(), password));
        
        User userAfterUpdate = getUserRepository().findById(userBeforeUpdate.getId()).get();
        assertNotEquals(userBeforeUpdate.getPassword(), userAfterUpdate.getPassword());
        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getUsername(), userAfterUpdate.getUsername());
        assertEquals(userBeforeUpdate.getRoles(), userAfterUpdate.getRoles());
    }
    
}
