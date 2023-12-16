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
import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
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
    public void setup(){
        super.setup();
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
        assertEquals(entity, userService.getByUsername(username));
    }

    @Test
    void getByUsername_error_usernameNotFound(){
        getUserRepository().deleteAll();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
            () -> userService.getByUsername(username));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

    @Test
    void getUserInfos() {
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfos(usernameContains, roleAdmin);
        assertEquals(1, foundUserInfos.size());
        assertEquals(expectedUserInfo, foundUserInfos.get(0));
    }

    @Test
    void getUserInfosbranch_usernameContainsAndRole() {
        saveSecondUser();
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfos(usernameContains, roleAdmin);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfosbranch_usernameContainsAndNull() {
        saveSecondUser();
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfos(usernameContains, null);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfosbranch_nullAndRole() {
        saveSecondUser();
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfos(null, roleAdmin);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfosbranch_nullAndNull() {
        saveSecondUser();
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfos(null, null);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfosbranch_usernameNotContainsAndRole() {
        List<UserInfo> foundUserInfos = userService.getUserInfos(usernameNotContains, roleAdmin);
        assertTrue(foundUserInfos.isEmpty());
    }

    @Test
    void getUserInfosbranch_usernameContainsAndRoleNotPresent() {
        List<UserInfo> foundUserInfos = userService.getUserInfos(usernameContains, roleUser);
        assertTrue(foundUserInfos.isEmpty());
    }

    @Test
    void getUserInfosbranch_usernameNotContainsAndRoleNotPresent() {
        List<UserInfo> foundUserInfos = userService.getUserInfos(usernameNotContains, roleUser);
        assertTrue(foundUserInfos.isEmpty());
    }

    @Test
    void save(){
        getUserRepository().deleteAll();
        assertDoesNotThrow(() -> userService.register(entity));
    }

    @Test
    void updateRoles() {
        User userBeforeUpdate = getUserRepository().save(entity);
        Set<String> enumRoles = new HashSet<>();
        enumRoles.add(roleManager);
        enumRoles.add(roleUser);

        assertDoesNotThrow(() -> userService.updateRoles(userBeforeUpdate.getUsername(), enumRoles));
        User userAfterUpdate = getUserRepository().findById(userBeforeUpdate.getId()).get();

        assertNotEquals(userBeforeUpdate.getRoles(), userAfterUpdate.getRoles());
        assertEquals(1, userBeforeUpdate.getRoles().size());
        assertEquals(2, userAfterUpdate.getRoles().size());
    }

    @Test
    void updateRoles_error_getByUsername() {
        getUserRepository().deleteAll();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
            () -> userService.updateRoles(username, new HashSet<>()));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
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
