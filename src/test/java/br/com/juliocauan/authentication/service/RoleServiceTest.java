package br.com.juliocauan.authentication.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    void givenValidName_WhenGetByName_ThenEqualsName(){
        for(EnumRole name : EnumRole.values())
            Assertions.assertEquals(name, roleService.getByName(name).getName());
    }

    @Test
    void givenInvalidName_WhenGetByName_ThenThrowsEntityNotFoundException(){
        EntityNotFoundException expection = Assertions
            .assertThrowsExactly(EntityNotFoundException.class, () -> roleService.getByName(null));
        Assertions.assertEquals("Role Not Found with name: null", expection.getMessage());
    }

}
