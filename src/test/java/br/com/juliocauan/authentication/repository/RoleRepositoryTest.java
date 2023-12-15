package br.com.juliocauan.authentication.repository;

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

    @Test
    void getByName_givenPresentName_thenIsPresent() {
        RoleEntity expectedEntity = getRoleRepository().findAll().get(0);
        assertEquals(expectedEntity, getRoleRepository().getByName(expectedEntity.getName()).get());
    }

    @Test
    void getByName_givenNotPresentName_thenIsNotPresent() {
        assertFalse(getRoleRepository().getByName("NOT_A_ROLE").isPresent());
    }

    @Test
    void getAll_givenPresentNameContains_thenNotEmpty() {
        List<Role> roles = assertDoesNotThrow(() -> getRoleRepository().getAll("ADMIN"));
        assertEquals(1, roles.size());
    }

    @Test
    void getAll_givenNullNameContains_thenNotEmpty() {
        List<Role> roles = assertDoesNotThrow(() -> getRoleRepository().getAll(null));
        assertEquals(1, roles.size());
    }

    @Test
    void getAll_givenNotPresentNameContains_thenEmpty() {
        List<Role> roles = assertDoesNotThrow(() -> getRoleRepository().getAll("NOT_A_ROLE"));
        assertTrue(roles.isEmpty());
    }

}
