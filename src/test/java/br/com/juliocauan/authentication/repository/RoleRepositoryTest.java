package br.com.juliocauan.authentication.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class RoleRepositoryTest extends TestContext {

    private RoleEntity entity;

    public RoleRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Override @BeforeAll
    public void setup() {
        super.setup();
        entity = getRoleRepository().findAll().get(0);
    }

    @Test
    void givenPresentName_WhenFindByName_ThenRole(){
        Assertions.assertEquals(entity, getRoleRepository().findByName(entity.getName()).get());
    }

    @Test
    void givenNotPresentName_WhenFindByName_ThenRoleNotPresent(){
        Assertions.assertFalse(getRoleRepository().findByName(null).isPresent());
    }

}
