package br.com.juliocauan.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;

class RoleServiceTest extends TestContext {

    private final RoleServiceImpl roleService;

    public RoleServiceTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, RoleServiceImpl roleService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.roleService = roleService;
    }

    @BeforeEach
    public void beforeEach() {
        getRoleRepository().deleteAll();
    }

    private final Role saveRole(String name) {
        return getRoleRepository().save(new Role(name));
    }

    @Test
    void getByName() {
        saveRole("ROLE");
        assertEquals("ROLE", roleService.getByName("ROLE").getName());
    }

    @Test
    void getByName_error_entityNotFound() {
        JpaObjectRetrievalFailureException expection = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
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
        List<Role> roles = getRoleRepository().findAll();
        assertEquals("ROLE", roles.get(0).getName());
    }

    @Test
    void register_error_entityExists() {
        saveRole("ROLE");
        DataIntegrityViolationException exception = assertThrowsExactly(DataIntegrityViolationException.class,
                () -> roleService.register("ROLE"));
        assertEquals("Role [ROLE] already exists!", exception.getMessage());
    }

    @Test
    void delete() {
        Role role = saveRole("ROLE");
        roleService.delete(role);
        assertTrue(getRoleRepository().findAll().isEmpty());
    }

}
