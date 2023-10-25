package br.com.juliocauan.authentication.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class RoleRepositoryTest extends TestContext {

    public RoleRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Test
    void givenPresentName_WhenFindByName_ThenRole(){
        RoleEntity expectedEntity = getRoleRepository().findAll().get(0);
        Assertions.assertEquals(expectedEntity, getRoleRepository().findByName(expectedEntity.getName()).get());
    }

    @Test
    void givenNotPresentName_WhenFindByName_ThenRoleNotPresent(){
        Assertions.assertFalse(getRoleRepository().findByName(null).isPresent());
    }

}
