package br.com.juliocauan.authentication.service.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AdminServiceImpl;
import br.com.juliocauan.authentication.util.UserMapper;
import jakarta.persistence.EntityNotFoundException;

class AdminServiceTest extends TestContext {

    private final AdminServiceImpl adminService;
    private final String role = getRandomString(15);

    public AdminServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AdminServiceImpl adminService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.adminService = adminService;
    }

    @BeforeEach
    public void beforeEach() {
        getUserRepository().deleteAll();
        getRoleRepository().deleteAll();
        saveRole(role);
    }

    private final UserEntity getUser() {
        return UserEntity
                .builder()
                .username(getRandomUsername())
                .password(getRandomPassword())
                .roles(Collections.singleton(new RoleEntity(getRoleRepository().getByName(role).get())))
                .build();
    }

    private final UserEntity saveUser() {
        return getUserRepository().save(getUser());
    }

    private final RoleEntity saveRole(String name) {
        return getRoleRepository().save(RoleEntity.builder().name(name).build());
    }

    private final Set<String> getRoleSet(String name) {
        return Collections.singleton(name);
    }

    @Test
    void getUserInfos() {
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(saveUser());
        List<UserInfo> userInfos = adminService.getUserInfos("@", role);
        assertEquals(1, userInfos.size());
        assertEquals(expectedUserInfo, userInfos.get(0));
    }

    @Test
    void getUserInfos_branch_usernameContainsAndRole() {
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(saveUser());
        saveUser();
        List<UserInfo> userInfos = adminService.getUserInfos("@", role);
        assertEquals(2, userInfos.size());
        assertTrue(userInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfos_branch_usernameContainsAndNull() {
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(saveUser());
        saveUser();
        List<UserInfo> userInfos = adminService.getUserInfos("@", null);
        assertEquals(2, userInfos.size());
        assertTrue(userInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfos_branch_nullAndRole() {
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(saveUser());
        saveUser();
        List<UserInfo> userInfos = adminService.getUserInfos(null, role);
        assertEquals(2, userInfos.size());
        assertTrue(userInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfos_branch_nullAndNull() {
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(saveUser());
        saveUser();
        List<UserInfo> userInfos = adminService.getUserInfos(null, null);
        assertEquals(2, userInfos.size());
        assertTrue(userInfos.contains(expectedUserInfo));
    }

    @Test
    void getUserInfos_branch_usernameNotContainsAndRole() {
        List<UserInfo> userInfos = adminService.getUserInfos("NOT_CONTAINS", role);
        assertTrue(userInfos.isEmpty());
    }

    @Test
    void getUserInfos_branch_usernameContainsAndRoleNotPresent() {
        List<UserInfo> userInfos = adminService.getUserInfos("@", "NOT_ROLE");
        assertTrue(userInfos.isEmpty());
    }

    @Test
    void getUserInfos_branch_usernameNotContainsAndRoleNotPresent() {
        List<UserInfo> userInfos = adminService.getUserInfos("NOT_CONTAINS", "NOT_ROLE");
        assertTrue(userInfos.isEmpty());
    }

    @Test
    void updateUserRoles() {
        User user = saveUser();
        String newRole = getRandomString(15);
        saveRole(newRole);
        adminService.updateUserRoles(user.getUsername(), getRoleSet(newRole));
        User userAfter = getUserRepository().findAll().get(0);

        assertEquals(user.getUsername(), userAfter.getUsername());
        assertNotEquals(user.getRoles().stream().findFirst().get(), userAfter.getRoles().stream().findFirst().get());
        assertEquals(newRole, userAfter.getRoles().stream().findFirst().get().getName());
    }

    @Test
    void updateUserRoles_error_usernameNotFound() {
        String username = getRandomUsername();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
                () -> adminService.updateUserRoles(username, getRoleSet(role)));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

    @Test
    void updateUserRoles_error_roleNotFound() {
        User user = saveUser();
        String newRole = getRandomString(15);
        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
                () -> adminService.updateUserRoles(user.getUsername(), getRoleSet(newRole)));
        assertEquals("Role [%s] not found!".formatted(newRole), exception.getMessage());
    }

    @Test
    void deleteUser() {
        User user = saveUser();
        
    }

}
