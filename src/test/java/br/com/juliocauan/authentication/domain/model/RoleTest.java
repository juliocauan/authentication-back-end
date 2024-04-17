package br.com.juliocauan.authentication.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;

class RoleTest extends TestContext {

    public RoleTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    private Role saveRole(String name) {
        return getRoleRepository().save(new Role(name));
    }

    @BeforeEach
    public void beforeEach() {
        getRoleRepository().deleteAll();
    }

    @Test
    void name_maxLength() {
        String maxLengthName = getRandomString(40);
        assertDoesNotThrow(() -> saveRole(maxLengthName));
        assertThrowsExactly(DataIntegrityViolationException.class, () -> saveRole(maxLengthName + "A"));
    }

    @Test
    void name_notNull() {
        assertThrowsExactly(DataIntegrityViolationException.class, () -> saveRole(null));
    }

    @Test
    void name_unique() {
        String roleName = getRandomString(15);
        saveRole(roleName);
        assertThrowsExactly(DataIntegrityViolationException.class, () -> saveRole(roleName));
        assertDoesNotThrow(() -> saveRole(roleName + "A"));
    }

    @Test
    void constructor_name() {
        String roleName = getRandomString(15);
        Role role = new Role(roleName);
        assertEquals(null, role.getId());
        assertEquals(roleName, role.getName());
    }

}
