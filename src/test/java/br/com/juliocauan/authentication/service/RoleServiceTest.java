package br.com.juliocauan.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.exception.AdminException;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

class RoleServiceTest extends TestContext {

    private final RoleServiceImpl roleService;

    public RoleServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, RoleServiceImpl roleService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.roleService = roleService;
    }

    @BeforeEach
    public void beforeEach() {
        getRoleRepository().deleteAll();
    }

    private final RoleEntity saveRole(String name) {
        return getRoleRepository().save(RoleEntity.builder().name(name).build());
    }

    @Test
    void getByName() {
        saveRole("ROLE");
        assertEquals("ROLE", roleService.getByName("ROLE").getName());
    }

    @Test
    void getByName_error_entityNotFound() {
        EntityNotFoundException expection = assertThrowsExactly(EntityNotFoundException.class,
                () -> roleService.getByName(null));
        assertEquals("Role [null] not found!", expection.getMessage());
    }

    @Test
    void getAll_givenPresentNameContains_thenNotEmpty() {
        saveRole("ROLE");
        List<Role> roles = roleService.getAll("ROLE");
        assertEquals(1, roles.size());
    }

    @Test
    void getAll_givenNullNameContains_thenNotEmpty() {
        saveRole("ROLE");
        List<Role> roles = roleService.getAll(null);
        assertEquals(1, roles.size());
    }

    @Test
    void getAll_givenNotPresentNameContains_thenEmpty() {
        saveRole("ROLE");
        List<Role> roles = roleService.getAll("AAA");
        assertTrue(roles.isEmpty());
    }

    @Test
    void register() {
        roleService.register("ROLE");
        List<RoleEntity> roles = getRoleRepository().findAll();
        assertEquals("ROLE", roles.get(0).getName());
    }

    @Test
    void register_error_entityExists() {
        saveRole("ROLE");
        EntityExistsException exception = assertThrowsExactly(EntityExistsException.class,
                () -> roleService.register("ROLE"));
        assertEquals("Role [ROLE] already exists!", exception.getMessage());
    }

    @Test
    void delete() {
        Role role = saveRole("ROLE");
        roleService.delete(role);
        assertTrue(getRoleRepository().findAll().isEmpty());
    }

    @Test
    void delete_error_adminException() {
        Role role = saveRole("ADMIN");
        AdminException exception = assertThrowsExactly(AdminException.class, () -> roleService.delete(role));
        assertEquals("Role [ADMIN] can not be deleted!", exception.getMessage());
    }

}
