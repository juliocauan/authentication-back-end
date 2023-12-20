package br.com.juliocauan.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

class UserServiceTest extends TestContext {

    private final UserServiceImpl userService;
    private final PasswordEncoder encoder;

    private Set<RoleEntity> roles = new HashSet<>();

    public UserServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, UserServiceImpl userService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.userService = userService;
        this.encoder = encoder;
    }

    @Override
    @BeforeAll
    public void beforeAll() {
        super.beforeAll();
        roles.add(getRoleRepository().findAll().get(0));
    }

    @BeforeEach
    void standard() {
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

    private final UserEntity saveUser() {
        return getUserRepository().save(getUser());
    }

    private final String getRoleName() {
        return roles.stream().findFirst().get().getName();
    }

    @Test
    void getByUsername() {
        User expectedUser = saveUser();
        assertEquals(expectedUser, userService.getByUsername(expectedUser.getUsername()));
    }

    @Test
    void getByUsername_error_usernameNotFound() {
        String username = getRandomUsername();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class,
                () -> userService.getByUsername(username));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

    @Test
    void getUsers() {
        User expectedUser = saveUser();
        List<User> foundUsers = userService.getUsers("@", getRoleName());
        assertEquals(1, foundUsers.size());
        assertEquals(expectedUser, foundUsers.get(0));
    }

    @Test
    void getUsersbranch_usernameContainsAndRole() {
        User expectedUser = saveUser();
        saveUser();
        List<User> foundUsers = userService.getUsers("@", getRoleName());
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getUsersbranch_usernameContainsAndNull() {
        User expectedUser = saveUser();
        saveUser();
        List<User> foundUsers = userService.getUsers("@", null);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getUsersbranch_nullAndRole() {
        User expectedUser = saveUser();
        saveUser();
        List<User> foundUsers = userService.getUsers(null, getRoleName());
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getUsersbranch_nullAndNull() {
        User expectedUser = saveUser();
        saveUser();
        List<User> foundUsers = userService.getUsers(null, null);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(expectedUser));
    }

    @Test
    void getUsersbranch_usernameNotContainsAndRole() {
        List<User> foundUsers = userService.getUsers("NOT_CONTAINS", getRoleName());
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getUsersbranch_usernameContainsAndRoleNotPresent() {
        List<User> foundUsers = userService.getUsers("@", "NOT_ROLE");
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void getUsersbranch_usernameNotContainsAndRoleNotPresent() {
        List<User> foundUsers = userService.getUsers("NOT_CONTAINS", "NOT_ROLE");
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void register() {
        User expectedUser = getUser();
        userService.register(expectedUser);
        User user = getUserRepository().findAll().get(0);

        assertEquals(expectedUser.getUsername(), user.getUsername());
        assertEquals(expectedUser.getRoles(), user.getRoles());
        assertTrue(encoder.matches(expectedUser.getPassword(), user.getPassword()));
    }

    @Test
    void register_error_entityExists() {
        User user = saveUser();
        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
                () -> userService.register(user));
        assertEquals(getErrorUsernameDuplicated(user.getUsername()), exception.getMessage());
    }

    @Test
    void register_error_passwordSecurity() {
        UserEntity user = getUser();
        user.setPassword("12345tyui");
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
                () -> userService.register(user));
        assertEquals("Password is not strong!", exception.getMessage());
    }

    @Test
    void update() {
        UserEntity expectedUser = saveUser();
        String newPassword = getRandomPassword();
        expectedUser.setPassword(newPassword);

        userService.update(expectedUser);
        UserEntity user = getUserRepository().findAll().get(0);
        assertEquals(expectedUser, user);
    }

    @Test
    void update_error_entityNotFound() {
        User user = getUser();
        EntityNotFoundException exception = assertThrowsExactly(EntityNotFoundException.class,
                () -> userService.update(user));
        assertEquals(getErrorUsernameNotFound(user.getUsername()), exception.getMessage());
    }

    @Test
    void delete() {
        User user = saveUser();
        userService.delete(user.getUsername());
        assertTrue(getUserRepository().findAll().isEmpty());
    }

    @Test
    void delete_error_usernameNotFound() {
        String username = getRandomUsername();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class, () -> userService.delete(username));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

    @Test
    void updatePassword() {
        User user = saveUser();
        String newPassword = getRandomPassword();
        userService.updatePassword(user.getUsername(), newPassword);
        assertTrue(encoder.matches(newPassword, getUserRepository().findAll().get(0).getPassword()));
    }

    @Test
    void updatePassword_error_usernameNotFound() {
        String username = getRandomUsername();
        String newPassword = getRandomPassword();
        UsernameNotFoundException exception = assertThrowsExactly(UsernameNotFoundException.class, () -> userService.updatePassword(username, newPassword));
        assertEquals(getErrorUsernameNotFound(username), exception.getMessage());
    }

    @Test
    void updatePassword_error_passwordSecurity() {
        User user = saveUser();
        String newPassword = "12345tyui";
        InvalidPasswordException exception = assertThrowsExactly(InvalidPasswordException.class,
                () -> userService.updatePassword(user.getUsername(), newPassword));
        assertEquals("Password is not strong!", exception.getMessage());
    }

}
