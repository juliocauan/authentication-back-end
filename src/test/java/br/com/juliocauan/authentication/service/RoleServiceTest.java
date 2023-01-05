package br.com.juliocauan.authentication.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import jakarta.persistence.EntityNotFoundException;

public class RoleServiceTest extends TestContext {
    
    private final RoleServiceImpl roleService;
    private final RoleRepositoryImpl roleRepository;

    public RoleServiceTest(RoleServiceImpl roleService, RoleRepositoryImpl roleRepository) {
        this.roleService = roleService;
        this.roleRepository = roleRepository;
    }

    @BeforeAll
    public void setup(){
        roleRepository.deleteAll();
        for(EnumRole name : EnumRole.values())
            roleRepository.save(RoleEntity.builder().name(name).build());
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
        EnumRole name = null;
        Assertions.assertThrows(EntityNotFoundException.class, () -> roleService.getByName(name), "Role Not Found with name: " + name);
    }

}
