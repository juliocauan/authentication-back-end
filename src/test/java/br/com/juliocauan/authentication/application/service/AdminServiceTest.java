package br.com.juliocauan.authentication.application.service;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.UserInfo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.exception.AdminException;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.mapper.UserMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

class AdminServiceTest extends TestContext {

    private final AdminService adminService;
    private final RoleService roleService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    private final Pageable pageable = PageRequest.ofSize(5);
    private final String rawPassword = getRandomPassword();

    public AdminServiceTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AdminService adminService,
            AuthenticationManager authenticationManager, PasswordEncoder encoder, RoleService roleService, UserService userService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.adminService = adminService;
        this.roleService = roleService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    @BeforeEach
    public void beforeEach() {
        getUserRepository().deleteAll();
        getRoleRepository().deleteAll();
        deauthenticate();
    }

    private final User authenticate() {
        String role = saveRole();
        User user = saveUser(role);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(),
                rawPassword);
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return user;
    }

    private final void deauthenticate() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private final User saveUser(String role) {
        User user = new User(getRandomUsername(), encoder.encode(rawPassword));
        user.setRoles(Collections.singleton(roleService.findByName(role)));
        return getUserRepository().save(user);
    }

    private final String saveRole() {
        return getRoleRepository().save(new Role(getRandomString(15))).getName();
    }

    private final Set<String> getRoleSet(String name) {
        return Collections.singleton(name);
    }

    @Test
    void findAllUsers() {
        String role = saveRole();
        UserInfo expectedUserInfo = UserMapper.INSTANCE.toUserInfo(saveUser(role));
        List<UserInfo> userInfos = adminService.findAllUsers("@", role, pageable);
        assertEquals(1, userInfos.size());
        assertEquals(expectedUserInfo, userInfos.get(0));
    }

    @Test
    void findAllUsers_branch_usernameContainsAndRole() {
        String role = saveRole();
        UserInfo expectedUserInfo = UserMapper.INSTANCE.toUserInfo(saveUser(role));
        saveUser(saveRole());
        List<UserInfo> userInfos = adminService.findAllUsers("@", role, pageable);
        assertEquals(1, userInfos.size());
        assertTrue(userInfos.contains(expectedUserInfo));
    }

    @Test
    void findAllUsers_branch_usernameContainsAndNull() {
        UserInfo expectedUserInfo = UserMapper.INSTANCE.toUserInfo(saveUser(saveRole()));
        saveUser(saveRole());
        List<UserInfo> userInfos = adminService.findAllUsers("@", null, pageable);
        assertEquals(2, userInfos.size());
        assertTrue(userInfos.contains(expectedUserInfo));
    }

    @Test
    void findAllUsers_branch_nullAndRole() {
        String role = saveRole();
        UserInfo expectedUserInfo = UserMapper.INSTANCE.toUserInfo(saveUser(role));
        saveUser(saveRole());
        List<UserInfo> userInfos = adminService.findAllUsers(null, role, pageable);
        assertEquals(1, userInfos.size());
        assertTrue(userInfos.contains(expectedUserInfo));
    }

    @Test
    void findAllUsers_branch_nullAndNull() {
        String role = saveRole();
        UserInfo expectedUserInfo = UserMapper.INSTANCE.toUserInfo(saveUser(role));
        saveUser(role);
        List<UserInfo> userInfos = adminService.findAllUsers(null, null, pageable);
        assertEquals(2, userInfos.size());
        assertTrue(userInfos.contains(expectedUserInfo));
    }

    @Test
    void findAllUsers_branch_usernameNotContainsAndRole() {
        String role = saveRole();
        List<UserInfo> userInfos = adminService.findAllUsers("NOT_CONTAINS", role, pageable);
        assertTrue(userInfos.isEmpty());
    }

    @Test
    void findAllUsers_branch_usernameContainsAndRoleNotPresent() {
        List<UserInfo> userInfos = adminService.findAllUsers("@", "NOT_ROLE", pageable);
        assertTrue(userInfos.isEmpty());
    }

    @Test
    void findAllUsers_branch_usernameNotContainsAndRoleNotPresent() {
        List<UserInfo> userInfos = adminService.findAllUsers("NOT_CONTAINS", "NOT_ROLE", pageable);
        assertTrue(userInfos.isEmpty());
    }

    @Test
    void findAllUsers_branch_pageable() {
        Integer expectedNumberOfUsers = pageable.getPageSize();
        for(int i = 0; i <= expectedNumberOfUsers; i++)
            saveUser(saveRole());

        List<UserInfo> userInfos = adminService.findAllUsers(null, null, pageable);
        assertEquals(expectedNumberOfUsers, userInfos.size());
        
        Pageable secondPage = PageRequest.of(1, pageable.getPageSize());
        userInfos = adminService.findAllUsers(null, null, secondPage);
        assertEquals(1, userInfos.size());
    }

    @Test
    void updateUserRoles() {
        authenticate();
        String oldRole = saveRole();
        String newRole = saveRole();
        User user = saveUser(oldRole);
        adminService.updateUserRoles(user.getUsername(), getRoleSet(newRole));
        User userAfter = userService.findByUsername(user.getUsername());

        assertEquals(user.getUsername(), userAfter.getUsername());
        assertNotEquals(user.getRoles().stream().findFirst().get(), userAfter.getRoles().stream().findFirst().get());
        assertEquals(newRole, userAfter.getRoles().stream().findFirst().get().getName());
    }

    @Test
    void updateUserRoles_branch_nullRole() {
        authenticate();
        String oldRole = saveRole();
        User user = saveUser(oldRole);
        adminService.updateUserRoles(user.getUsername(), new HashSet<>());
        User userAfter = userService.findByUsername(user.getUsername());

        assertEquals(user.getUsername(), userAfter.getUsername());
        assertTrue(userAfter.getRoles().isEmpty());
    }

    @Test
    void updateUserRoles_error_updateSelfAdminException() {
        User user = authenticate();
        String role = saveRole();
        AdminException exception = assertThrowsExactly(AdminException.class,
                () -> adminService.updateUserRoles(user.getUsername(), getRoleSet(role)));
        assertEquals("You can not update/delete your own account here!", exception.getMessage());
    }

    @Test
    void updateUserRoles_error_usernameNotFound() {
        authenticate();
        String username = getRandomUsername();
        String role = saveRole();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
                () -> adminService.updateUserRoles(username, getRoleSet(role)));
        assertEquals("Username [%s] not found!".formatted(username), exception.getMessage());
    }

    @Test
    void updateUserRoles_error_roleNotFound() {
        authenticate();
        String role = saveRole();
        User user = saveUser(role);
        String newRole = getRandomString(15);
        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
                () -> adminService.updateUserRoles(user.getUsername(), getRoleSet(newRole)));
        assertEquals("Role [%s] not found!".formatted(newRole), exception.getMessage());
    }

    @Test
    void disableUser() {
        authenticate();
        String role = saveRole();
        User user = saveUser(role);
        assertFalse(user.isDisabled());
        adminService.disableUser(user.getUsername());
        user = getUserRepository().findById(user.getId()).get();
        assertTrue(user.isDisabled());
    }

    @Test
    void disableUser_error_notAuthenticated() {
        deauthenticate();
        assertThrowsExactly(NullPointerException.class,
                () -> adminService.disableUser(getRandomUsername()));
    }

    @Test
    void disableUser_error_disableSelfAdminException() {
        User user = authenticate();
        AdminException exception = assertThrowsExactly(AdminException.class,
                () -> adminService.disableUser(user.getUsername()));
        assertEquals("You can not update/delete your own account here!", exception.getMessage());
    }

    @Test
    void findAllRoles() {
        String role = saveRole();
        List<String> roles = adminService.findAllRoles(role.substring(role.length() / 2));
        assertEquals(1, roles.size());
        assertEquals(role, roles.get(0));
    }

    @Test
    void findAllRoles_branch_nameContains() {
        String role = saveRole();
        List<String> roles = adminService.findAllRoles(role.substring(role.length() / 2));
        assertEquals(1, roles.size());
        assertEquals(role, roles.get(0));
    }

    @Test
    void findAllRoles_branch_nullNameContains() {
        String role = saveRole();
        List<String> roles = adminService.findAllRoles(null);
        assertEquals(role, roles.get(0));
    }

    @Test
    void registerRole() {
        String newRole = getRandomString(15);
        adminService.registerRole(newRole);
        assertEquals(newRole, getRoleRepository().findAll().get(0).getName());
    }

    @Test
    void registerRole_error_entityExists() {
        String role = saveRole();
        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
                () -> adminService.registerRole(role));
        assertEquals("Role [%s] already exists!".formatted(role), exception.getMessage());
    }

    @Test
    void deleteRole() {
        String role = saveRole();
        User user = saveUser(role);
        assertEquals(getRoleSet(role), user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        adminService.deleteRole(role);
        assertTrue(getRoleRepository().findAll().isEmpty());
        assertTrue(getUserRepository().findAll().get(0).getRoles().isEmpty());
    }

    @Test
    void deleteRole_error_adminException() {
        String roleAdmin = "ADMIN";
        getRoleRepository().save(new Role(roleAdmin));
        saveUser(roleAdmin);

        AdminException exception = assertThrowsExactly(AdminException.class, () -> adminService.deleteRole(roleAdmin));
        assertEquals("Role [ADMIN] can not be deleted!", exception.getMessage());
        assertEquals(getRoleSet(roleAdmin), getUserRepository().findAll().get(0).getRoles().stream().map(Role::getName)
                .collect(Collectors.toSet()));
    }

    @Test
    void deleteRole_error_roleNotFound() {
        String role = "NOT_PRESENT_ROLE";

        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
            () -> adminService.deleteRole(role));
        assertEquals("Role [%s] not found!".formatted(role), exception.getMessage());
    }

}
