package br.com.juliocauan.authentication.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class RoleEntityTest extends TestContext {

    private final String defaultRoleName = "MANAGER";

    public RoleEntityTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    private RoleEntity saveRole(String name) {
        return getRoleRepository().save(RoleEntity.builder().name(name).build());
    }

    @BeforeEach
    public void standard() {
        getRoleRepository().deleteAll();
    }

    @Test
    void save() {
        assertDoesNotThrow(() -> saveRole(defaultRoleName));
    }

    @Test
    void save_constraint_length() {
        String maxLengthName = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        assertDoesNotThrow(() -> saveRole(maxLengthName));
        assertThrowsExactly(DataIntegrityViolationException.class, () -> saveRole(maxLengthName + "A"));
    }

    @Test
    void save_constraint_notNull() {
        assertThrowsExactly(DataIntegrityViolationException.class, () -> saveRole(null));
    }

    @Test
    void save_constraint_unique() {
        saveRole(defaultRoleName);
        assertThrowsExactly(DataIntegrityViolationException.class, () -> saveRole(defaultRoleName));
        assertDoesNotThrow(() -> saveRole(defaultRoleName + "A"));
    }
    
}
