package br.com.juliocauan.authentication.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;

class RoleRepositoryTest extends TestContext {

    public RoleRepositoryTest(UserRepositoryImpl userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @BeforeEach
    public void beforeEach() {
        getRoleRepository().deleteAll();
    }

    private final Role saveRole(String name) {
        return getRoleRepository().save(new Role(name));
    }

    @Test
    void findByName_givenPresentName_thenIsPresent() {
        Role expectedEntity = saveRole("ROLE");
        assertEquals(expectedEntity, getRoleRepository().findByName("ROLE"));
    }

    @Test
    void findByName_givenNotPresentName_thenException() {
        saveRole("ROLE");
        JpaObjectRetrievalFailureException exception = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
            () -> getRoleRepository().findByName("NOT_A_ROLE"));
        assertEquals("Role [NOT_A_ROLE] not found!", exception.getMessage());
    }

    @Test
    void findAllWithFilters_givenPresentNameContains_thenNotEmptyList() {
        Role role = saveRole("ROLE");
        List<Role> roles = getRoleRepository().findAllWithFilters("ROLE");
        assertEquals(1, roles.size());
        assertEquals(role, roles.get(0));
    }

    @Test
    void findAllWithFilters_givenNullNameContains_thenNotEmptyList() {
        saveRole("ROLE");
        List<Role> roles = getRoleRepository().findAllWithFilters(null);
        assertEquals(1, roles.size());
    }

    @Test
    void findAllWithFilters_givenNotPresentNameContains_thenEmptyList() {
        saveRole("ROLE");
        List<Role> roles = getRoleRepository().findAllWithFilters("AAA");
        assertTrue(roles.isEmpty());
    }

    @Test
    void register() {
        getRoleRepository().register("ROLE");
        Role role = getRoleRepository().findAll().get(0);
        assertEquals("ROLE", role.getName());
    }

    @Test
    void register_givenExistingRoleName_thenException() {
        getRoleRepository().register("ROLE");
        DataIntegrityViolationException exception = assertThrowsExactly(DataIntegrityViolationException.class,
            () -> getRoleRepository().register("ROLE"));
        assertEquals("Role [ROLE] already exists!", exception.getMessage());
    }

    @Test
    void delete() {
        Role role = saveRole("ROLE");
        getRoleRepository().delete(role);
        assertTrue(getRoleRepository().findAll().isEmpty());
    }

}
