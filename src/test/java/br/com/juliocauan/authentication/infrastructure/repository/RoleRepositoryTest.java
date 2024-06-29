package br.com.juliocauan.authentication.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;

class RoleRepositoryTest extends TestContext {

    public RoleRepositoryTest(UserRepository userRepository, RoleRepository roleRepository,
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
    void findByName() {
        Role expectedEntity = saveRole("ROLE");
        assertEquals(expectedEntity, getRoleRepository().findByName("ROLE"));
    }

    @Test
    void findByName_error_notPresentName() {
        saveRole("ROLE");
        JpaObjectRetrievalFailureException exception = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
            () -> getRoleRepository().findByName("NOT_A_ROLE"));
        assertEquals("Role [NOT_A_ROLE] not found!", exception.getMessage());
    }

    @Test
    void findByName_error_nullName() {
        saveRole("null");
        JpaObjectRetrievalFailureException exception = assertThrowsExactly(JpaObjectRetrievalFailureException.class,
            () -> getRoleRepository().findByName(null));
        assertEquals("Role [null] not found!", exception.getMessage());
    }

    @Test
    void findAllByFilters() {
        Role role = saveRole("ROLE");
        Set<Role> roles = getRoleRepository().findAllByFilters("ROLE");
        assertEquals(1, roles.size());
        assertTrue(roles.contains(role));
    }

    @Test
    void findAllByFilters_branch_nullNameContains() {
        saveRole("ROLE");
        Set<Role> roles = getRoleRepository().findAllByFilters(null);
        assertEquals(1, roles.size());
    }

    @Test
    void findAllByFilters_branch_notPresentNameContains() {
        saveRole("ROLE");
        Set<Role> roles = getRoleRepository().findAllByFilters("AAA");
        assertTrue(roles.isEmpty());
    }

    @Test
    void register() {
        getRoleRepository().register("ROLE");
        Role role = getRoleRepository().findAll().get(0);
        assertEquals("ROLE", role.getName());
    }

    @Test
    void register_error_roleAlreadyExists() {
        getRoleRepository().register("ROLE");
        DataIntegrityViolationException exception = assertThrowsExactly(DataIntegrityViolationException.class,
            () -> getRoleRepository().register("ROLE"));
        assertEquals("Role [ROLE] already exists!", exception.getMessage());
    }

    @Test
    void register_error_nullName() {
        assertThrowsExactly(DataIntegrityViolationException.class, () -> getRoleRepository().register(null));
    }

}
