package br.com.juliocauan.authentication.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import jakarta.persistence.EntityNotFoundException;

class RoleServiceTest extends TestContext {

    private final RoleServiceImpl roleService;
    
    public RoleServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, RoleServiceImpl roleService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.roleService = roleService;
    }

    @Test
    void getByName(){
        assertEquals("ADMIN", roleService.getByName("ADMIN").getName());
    }

    @Test
    void getByName_error_entityNotFound(){
        EntityNotFoundException expection = assertThrowsExactly(EntityNotFoundException.class,
            () -> roleService.getByName(null));
        assertEquals("Role [null] not found!", expection.getMessage());
    }

}
