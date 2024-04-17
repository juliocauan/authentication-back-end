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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class UserRepositoryTest extends TestContext {

    private final Pageable pageable = PageRequest.ofSize(5);
    private Set<Role> roles = new HashSet<>();
    
    public UserRepositoryTest(UserRepositoryImpl userRepository, RoleRepository roleRepository,
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
        return new User(getRandomUsername(null), getRandomPassword(), roles);
    }

    private final User saveUser() {
        return getUserRepository().save(getUser());
    }

    private final String getRoleName() {
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
        String roleName = getRoleName();

        List<User> foundUsers = getUserRepository().getAll("@", roleName, pageable);

        assertEquals(1, foundUsers.size());
        assertEquals(expectedUser, foundUsers.get(0));
    }

    @Test
    void getAll_branch_usernameContainsAndRole() {
        User expectedUser = saveUser();
        saveUser();
        String roleName = getRoleName();

        List<User> foundUsers = getUserRepository().getAll("@", roleName, pageable);

        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getAll_branch_usernameContainsAndNull() {
        User expectedUser = saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().getAll("@", null, pageable);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getAll_branch_nullAndRole() {
        User expectedUser = saveUser();
        saveUser();
        String roleName = getRoleName();

        List<User> foundUsers = getUserRepository().getAll(null, roleName, pageable);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getAll_branch_nullAndNull() {
        User expectedUser = saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().getAll(null, null, pageable);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getAll_branch_usernameNotContainsAndRole() {
        List<User> foundUsers = getUserRepository().getAll("NOT_CONTAINS", getRoleName(), pageable);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getAll_branch_usernameContainsAndRoleNotPresent() {
        List<User> foundUsers = getUserRepository().getAll("@", "NOT_A_ROLE", pageable);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getAll_branch_usernameNotContainsAndRoleNotPresent() {
        List<User> foundUsers = getUserRepository().getAll("NOT_CONTAINS", "NOT_A_ROLE", pageable);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getAll_branch_pageable() {
        Integer expectedNumberOfUsers = pageable.getPageSize();
        for(int i = 0; i <= expectedNumberOfUsers; i++)
            saveUser();

        List<User> foundUsers = getUserRepository().getAll(null, null, pageable);
        assertEquals(expectedNumberOfUsers, foundUsers.size());
        
        Pageable secondPage = PageRequest.of(1, pageable.getPageSize());
        foundUsers = getUserRepository().getAll(null, null, secondPage);
        assertEquals(1, foundUsers.size());
    }

    @Test
    void getAll_branch_onlyRoleName() {
        User expectedUser = saveUser();
        String roleName = getRoleName();

        List<User> foundUsers = getUserRepository().getAll(roleName);

        assertEquals(1, foundUsers.size());
        assertEquals(expectedUser, foundUsers.get(0));
    }

    @Test
    void getAll_branch_onlyRoleNameNull() {
        saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().getAll(null);

        assertEquals(2, foundUsers.size());
    }

    @Test
    void getAll_branch_onlyRoleIncorrect() {
        saveUser();
        saveUser();

        List<User> foundUsers = getUserRepository().getAll("NOT_A_ROLE");

        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void register() {
        User expectedUser = getUser();
        getUserRepository().register(expectedUser);
        User user = getUserRepository().findAll().get(0);
        assertEquals(expectedUser, user);
    }

    @Test
    void delete() {
        User user = saveUser();
        getUserRepository().delete(user);
        assertTrue(getUserRepository().findAll().isEmpty());
    }

}
