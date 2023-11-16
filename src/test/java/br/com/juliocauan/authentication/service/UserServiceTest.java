package br.com.juliocauan.authentication.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
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
    private final EnumRole roleAdmin = EnumRole.ADMIN;
    private final EnumRole roleUser = EnumRole.USER;

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
        roles.add(new RoleEntity(getRoleRepository().getByName(roleAdmin).get()));
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        entity = getUserRepository().save(
            UserEntity.builder()
                .password(password)
                .username(username)
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
    void save(){
        getUserRepository().deleteAll();
        assertDoesNotThrow(() -> userService.save(entity));
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
        assertEquals(errorUsernameNotFound, exception.getMessage());
    }

    @Test
    void checkDuplicatedUsername(){
        getUserRepository().deleteAll();
        assertDoesNotThrow(() -> userService.checkDuplicatedUsername(username));
    }

    @Test
    void checkDuplicatedUsername_entityExistsException(){
        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
            () -> userService.checkDuplicatedUsername(username));
        assertEquals(errorDuplicatedUsername, exception.getMessage());
    }

    @Test
    void getUserInfos() {
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfosByUsernameSubstringAndRole(usernameContains, roleAdmin);
        assertEquals(1, foundUserInfos.size());
        assertEquals(expectedUserInfo, foundUserInfos.get(0));
    }

    @Test
    void getUserInfos_branch_usernameContainsAndRole() {
        saveSecondUser();
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfosByUsernameSubstringAndRole(usernameContains, roleAdmin);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfos_branch_usernameContainsAndNull() {
        saveSecondUser();
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfosByUsernameSubstringAndRole(usernameContains, null);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfos_branch_nullAndRole() {
        saveSecondUser();
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfosByUsernameSubstringAndRole(null, roleAdmin);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfos_branch_nullAndNull() {
        saveSecondUser();
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> foundUserInfos = userService.getUserInfosByUsernameSubstringAndRole(null, null);
        assertEquals(2, foundUserInfos.size());
        assertTrue(foundUserInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfos_branch_usernameNotContainsAndRole() {
        List<UserInfo> foundUserInfos = userService.getUserInfosByUsernameSubstringAndRole(usernameNotContains, roleAdmin);
        assertTrue(foundUserInfos.isEmpty());
    }

    @Test
    void getUserInfos_branch_usernameContainsAndRoleNotPresent() {
        List<UserInfo> foundUserInfos = userService.getUserInfosByUsernameSubstringAndRole(usernameContains, roleUser);
        assertTrue(foundUserInfos.isEmpty());
    }

    @Test
    void getUserInfos_branch_usernameNotContainsAndRoleNotPresent() {
        List<UserInfo> foundUserInfos = userService.getUserInfosByUsernameSubstringAndRole(usernameNotContains, roleUser);
        assertTrue(foundUserInfos.isEmpty());
    }

    //TODO
    // @Test
    // void updateRoles() {
    //     UserEntity userBeforeUpdate = getUserRepository().save(entity);
    //     UpdateUserRolesForm alterUserRolesForm = new UpdateUserRolesForm()
    //         .username(username)
    //         .addRolesItem(roleManager)
    //         .addRolesItem(roleUser);
    //     assertDoesNotThrow(() -> userService.updateRole(userBeforeUpdate, roles));
    //     UserEntity userAfterUpdate = getUserRepository().findById(userBeforeUpdate.getId()).get();

    //     assertNotEquals(userBeforeUpdate.getRoles(), userAfterUpdate.getRoles());
    //     assertEquals(1, userBeforeUpdate.getRoles().size());
    //     assertEquals(2, userAfterUpdate.getRoles().size());
    // }

    // @Test
    // void updateRoles_error_getByUsername() {
    //     UpdateUserRolesForm alterUserRolesForm = new UpdateUserRolesForm()
    //         .username(username)
    //         .addRolesItem(roleManager)
    //         .addRolesItem(roleUser);
    //     UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
    //         () -> adminService.updateUserRole(alterUserRolesForm));
    //     assertEquals(usernameNotFoundException, exception.getMessage());
    // }
    
}
