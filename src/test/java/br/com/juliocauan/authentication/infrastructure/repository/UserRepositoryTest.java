package br.com.juliocauan.authentication.infrastructure.repository;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordException;

class UserRepositoryTest extends TestContext {

    private final Pageable pageable = PageRequest.ofSize(5);
    private final Set<Role> roles = new HashSet<>();
    
    public UserRepositoryTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Override @BeforeAll
    public void beforeAll(){
        super.beforeAll();
        roles.add(getRoleRepository().findAll().get(0));
    }

    @BeforeEach
    void beforeEach(){
        getUserRepository().deleteAll();
    }

    private final User getUser() {
        User user = new User(getRandomUsername(), getRandomPassword());
        user.setRoles(roles);
        return user;
    }

    private final User saveUser() {
        return getUserRepository().save(getUser());
    }

    private final String getRoleName() {
        return roles.stream().findFirst().get().getName();
    }

    @Test
    void findByUsername(){
        User user = saveUser();
        assertEquals(user, getUserRepository().findByUsername(user.getUsername()));
    }

    @Test
    void findByUsername_error_notPresentUsername(){
        String username = getRandomUsername();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
            () -> getUserRepository().findByUsername(username));
        assertEquals("Username [%s] not found!".formatted(username), exception.getMessage());
    }

    @Test
    void findByUsername_error_nullUsername(){
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
            () -> getUserRepository().findByUsername(null));
        assertEquals("Username [null] not found!", exception.getMessage());
    }

    @Test
    void findAllByFilters() {
        User expectedUser = saveUser();
        String roleName = getRoleName();

        List<User> foundUsers = getUserRepository().findAllByFilters("@", roleName, pageable);

        assertEquals(1, foundUsers.size());
        assertEquals(expectedUser, foundUsers.get(0));
    }

    @Test
    void findAllByFilters_branch_usernameContainsAndRole() {
        User expectedUser = saveUser();
        saveUser();
        String roleName = getRoleName();

        List<User> foundUsers = getUserRepository().findAllByFilters("@", roleName, pageable);

        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void findAllByFilters_branch_usernameContainsAndNull() {
        User expectedUser = saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().findAllByFilters("@", null, pageable);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void findAllByFilters_branch_nullAndRole() {
        User expectedUser = saveUser();
        saveUser();
        String roleName = getRoleName();

        List<User> foundUsers = getUserRepository().findAllByFilters(null, roleName, pageable);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void findAllByFilters_branch_nullAndNull() {
        User expectedUser = saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().findAllByFilters(null, null, pageable);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void findAllByFilters_branch_usernameNotContainsAndRole() {
        List<User> foundUsers = getUserRepository().findAllByFilters("NOT_CONTAINS", getRoleName(), pageable);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void findAllByFilters_branch_usernameContainsAndRoleNotPresent() {
        List<User> foundUsers = getUserRepository().findAllByFilters("@", "NOT_A_ROLE", pageable);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void findAllByFilters_branch_usernameNotContainsAndRoleNotPresent() {
        List<User> foundUsers = getUserRepository().findAllByFilters("NOT_CONTAINS", "NOT_A_ROLE", pageable);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void findAllByFilters_branch_pageable() {
        Integer expectedNumberOfUsers = pageable.getPageSize();
        for(int i = 0; i <= expectedNumberOfUsers; i++)
            saveUser();

        List<User> foundUsers = getUserRepository().findAllByFilters(null, null, pageable);
        assertEquals(expectedNumberOfUsers, foundUsers.size());
        
        Pageable secondPage = PageRequest.of(1, pageable.getPageSize());
        foundUsers = getUserRepository().findAllByFilters(null, null, secondPage);
        assertEquals(1, foundUsers.size());
    }

    @Test
    void findAllByRole_branch_onlyRoleName() {
        User expectedUser = saveUser();
        String roleName = getRoleName();

        List<User> foundUsers = getUserRepository().findAllByRole(roleName);

        assertEquals(1, foundUsers.size());
        assertEquals(expectedUser, foundUsers.get(0));
    }

    @Test
    void findAllByRole_branch_onlyRoleNameNull() {
        saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().findAllByRole(null);

        assertEquals(2, foundUsers.size());
    }

    @Test
    void findAllByRole_branch_onlyRoleIncorrect() {
        saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().findAllByRole("NOT_A_ROLE");

        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void register() {
        User expectedUser = getUser();
        getUserRepository().register(expectedUser);
        User actualUser = getUserRepository().findAll().get(0);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void register_error_userAlreadyExists() {
        User user = getUser();
        getUserRepository().register(user);
        DataIntegrityViolationException exception = assertThrowsExactly(DataIntegrityViolationException.class,
            () -> getUserRepository().register(user));
        assertEquals("Username [%s] is already taken!".formatted(user.getUsername()), exception.getMessage());
    }

    @Test
    void register_error_weakPassword() {
        User user = getUser();
        user.setPassword("123456789");
        PasswordException exception = assertThrowsExactly(PasswordException.class,
            () -> getUserRepository().register(user));
        assertEquals("Password is not strong!", exception.getMessage());
    }

    @Test
    void updateUserRoles() {
        User expectedUser = saveUser();
        Set<Role> newRoles = new HashSet<>();
        expectedUser.setRoles(newRoles);
        getUserRepository().updateUserRoles(expectedUser, newRoles);
        User actualUser = getUserRepository().findAll().get(0);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updatePassword() {
        getUserRepository().register(getUser());
        User expectedUser = getUserRepository().findAll().get(0);
        String oldPassword = expectedUser.getPassword();
        String newPassword = getRandomPassword();
        getUserRepository().updatePassword(expectedUser, newPassword);
        User actualUser = getUserRepository().findAll().get(0);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getPassword(), actualUser.getPassword());
        assertNotEquals(oldPassword, actualUser.getPassword());
    }

    @Test
    void updatePassword_error_weakPassword() {
        User user = saveUser();
        PasswordException exception = assertThrowsExactly(PasswordException.class,
            () -> getUserRepository().updatePassword(user, "123456789"));
        assertEquals("Password is not strong!", exception.getMessage());
    }

    @Test
    void deleteByUsername() {
        User user = saveUser();
        getUserRepository().deleteByUsername(user.getUsername());
        assertTrue(getUserRepository().findAll().isEmpty());
    }

}
