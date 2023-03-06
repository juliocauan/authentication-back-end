package br.com.juliocauan.authentication.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import jakarta.persistence.EntityNotFoundException;

public class RoleServiceTest extends TestContext {

    private final RoleServiceImpl roleService;
    
    public RoleServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, RoleServiceImpl roleService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.roleService = roleService;
    }

    @Test
    public void whenGetRepository_ThenInstanceOfRoleRepository(){
        Assertions.assertInstanceOf(RoleRepository.class, roleService.getRepository());
    }

    @Test
    public void givenValidName_WhenGetByName_ThenEqualsName(){
        for(EnumRole name : EnumRole.values())
            Assertions.assertEquals(name, roleService.getByName(name).getName());
    }

    @Test
    public void givenInvalidName_WhenGetByName_ThenThrowsEntityNotFoundException(){
        Assertions.assertThrows(EntityNotFoundException.class, () -> roleService.getByName(null), "Role Not Found with name: null");
    }

}
