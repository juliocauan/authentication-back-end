package br.com.juliocauan.authentication.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class RoleRepositoryTest extends TestContext {
    
    private final UserRepositoryImpl userRepository;
    private final RoleRepositoryImpl roleRepository;

    private RoleEntity entity;

    public RoleRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @BeforeAll
    public void setup(){
        userRepository.deleteAll();
        roleRepository.deleteAll();
        for(EnumRole name : EnumRole.values())
            roleRepository.save(RoleEntity.builder().name(name).build());
        entity = roleRepository.findAll().get(0);
    }

    @Test
    public void givenPresentName_WhenFindByName_ThenRole(){
        Assertions.assertEquals(entity, roleRepository.findByName(entity.getName()).get());
    }

    @Test
    public void givenNotPresentName_WhenFindByName_ThenRoleNotPresent(){
        Assertions.assertFalse(roleRepository.findByName(null).isPresent());
    }

}
