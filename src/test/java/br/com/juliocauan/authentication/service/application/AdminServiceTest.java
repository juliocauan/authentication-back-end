package br.com.juliocauan.authentication.service.application;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.AlterUserRolesForm;
import org.openapitools.model.EnumRole;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AdminServiceImpl;

class AdminServiceTest extends TestContext {

    private final AdminServiceImpl adminService;

    private final String password = "12345678";
    private final String username = "test@email.com";
    private final EnumRole roleManager = EnumRole.MANAGER;
    private final EnumRole roleUser = EnumRole.USER;
    private final String usernameNotFoundException = "User Not Found with username: " + username;

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();

    public AdminServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AdminServiceImpl adminService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.adminService = adminService;
    }

    @Override @BeforeAll
    public void setup(){
        super.setup();
        roles.add(new RoleEntity(getRoleRepository().getByName(roleManager).get()));
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        entity = UserEntity.builder()
            .password(password)
            .username(username)
            .roles(roles)
        .build();
    }

    @Test
    void alterUserRole() {
        UserEntity userBeforeUpdate = getUserRepository().save(entity);
        AlterUserRolesForm alterUserRolesForm = new AlterUserRolesForm()
            .username(username)
            .addRolesItem(roleManager)
            .addRolesItem(roleUser);
        Assertions.assertDoesNotThrow(() -> adminService.updateUserRole(alterUserRolesForm));
        UserEntity userAfterUpdate = getUserRepository().findById(userBeforeUpdate.getId()).get();

        Assertions.assertNotEquals(userBeforeUpdate.getRoles(), userAfterUpdate.getRoles());
        Assertions.assertEquals(1, userBeforeUpdate.getRoles().size());
        Assertions.assertEquals(2, userAfterUpdate.getRoles().size());
    }

    @Test
    void alterUserRole_error_getByUsername() {
        AlterUserRolesForm alterUserRolesForm = new AlterUserRolesForm()
            .username(username)
            .addRolesItem(roleManager)
            .addRolesItem(roleUser);
        UsernameNotFoundException exception = Assertions.assertThrowsExactly(UsernameNotFoundException.class,
            () -> adminService.updateUserRole(alterUserRolesForm));
        Assertions.assertEquals(usernameNotFoundException, exception.getMessage());
    }
    
}
