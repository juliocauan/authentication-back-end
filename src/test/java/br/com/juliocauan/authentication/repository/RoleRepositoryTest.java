package br.com.juliocauan.authentication.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class RoleRepositoryTest extends TestContext {

    public RoleRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @BeforeEach
    public void beforeEach() {
        getRoleRepository().deleteAll();
    }

    private final RoleEntity saveRole(String name) {
        return getRoleRepository().save(new RoleEntity(name));
    }

    @Test
    void getByName_givenPresentName_thenIsPresent() {
        RoleEntity expectedEntity = saveRole("ROLE");
        assertEquals(expectedEntity, getRoleRepository().getByName("ROLE").get());
    }

    @Test
    void getByName_givenNotPresentName_thenIsNotPresent() {
        assertFalse(getRoleRepository().getByName("NOT_A_ROLE").isPresent());
    }

    @Test
    void getAll_givenPresentNameContains_thenNotEmpty() {
        saveRole("ROLE");
        List<Role> roles = getRoleRepository().getAll("ROLE");
        assertEquals(1, roles.size());
    }

    @Test
    void getAll_givenNullNameContains_thenNotEmpty() {
        saveRole("ROLE");
        List<Role> roles = getRoleRepository().getAll(null);
        assertEquals(1, roles.size());
    }

    @Test
    void getAll_givenNotPresentNameContains_thenEmpty() {
        saveRole("ROLE");
        List<Role> roles = getRoleRepository().getAll("AAA");
        assertTrue(roles.isEmpty());
    }

    @Test
    void register() {
        getRoleRepository().register("ROLE");
        RoleEntity role = getRoleRepository().findAll().get(0);
        assertEquals("ROLE", role.getName());
    }

    @Test
    void delete() {
        Role role = saveRole("ROLE");
        getRoleRepository().delete(role);
        assertTrue(getRoleRepository().findAll().isEmpty());
    }

}
