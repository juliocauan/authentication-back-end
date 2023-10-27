package br.com.juliocauan.authentication.service.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.AlterUserRolesForm;
import org.openapitools.model.EnumRole;
import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AdminServiceImpl;

class AdminServiceTest extends TestContext {

    private final AdminServiceImpl adminService;

    private final String password = "12345678";
    private final String username = "test@email.com";
    private final String usernameContains = "test";
    private final String usernameNotContains = "asd";
    private final EnumRole role1 = EnumRole.MANAGER;
    private final EnumRole role2 = EnumRole.USER;
    private final EnumRole roleNotPresent = EnumRole.ADMIN;
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
        roles.add(new RoleEntity(getRoleRepository().findByName(role1).get()));
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
    void getUserInfos(){
        getUserRepository().save(entity);
        UserInfo expectedUserInfo = UserMapper.domainToUserInfo(entity);
        List<UserInfo> userInfoList = adminService.getUserInfos(usernameContains, role1);
        Assertions.assertEquals(1, userInfoList.size());
        Assertions.assertEquals(expectedUserInfo, userInfoList.get(0));
        
        entity = UserEntity.builder()
            .username(username + "2")
            .password(password)
            .roles(roles)
        .build();
        getUserRepository().save(entity);

        userInfoList = adminService.getUserInfos(usernameContains, role1);
        Assertions.assertEquals(2, userInfoList.size());
        Assertions.assertTrue(userInfoList.contains(expectedUserInfo));

        userInfoList = adminService.getUserInfos(null, null);
        Assertions.assertEquals(2, userInfoList.size());

        userInfoList = adminService.getUserInfos(null, role1);
        Assertions.assertEquals(2, userInfoList.size());

        userInfoList = adminService.getUserInfos(usernameContains, null);
        Assertions.assertEquals(2, userInfoList.size());

        userInfoList = adminService.getUserInfos(usernameNotContains, null);
        Assertions.assertTrue(userInfoList.isEmpty());

        userInfoList = adminService.getUserInfos(null, roleNotPresent);
        Assertions.assertTrue(userInfoList.isEmpty());
    }

    @Test
    void alterUserRole() {
        UserEntity userBeforeUpdate = getUserRepository().save(entity);
        AlterUserRolesForm alterUserRolesForm = new AlterUserRolesForm()
            .username(username)
            .addRolesItem(role1)
            .addRolesItem(role2);
        Assertions.assertDoesNotThrow(() -> adminService.alterUserRole(alterUserRolesForm));
        UserEntity userAfterUpdate = getUserRepository().findById(userBeforeUpdate.getId()).get();

        Assertions.assertNotEquals(userBeforeUpdate.getRoles(), userAfterUpdate.getRoles());
        Assertions.assertEquals(1, userBeforeUpdate.getRoles().size());
        Assertions.assertEquals(2, userAfterUpdate.getRoles().size());
    }

    @Test
    void alterUserRole_error_getByUsername() {
        AlterUserRolesForm alterUserRolesForm = new AlterUserRolesForm()
            .username(username)
            .addRolesItem(role1)
            .addRolesItem(role2);
        UsernameNotFoundException exception = Assertions.assertThrowsExactly(UsernameNotFoundException.class,
            () -> adminService.alterUserRole(alterUserRolesForm));
        Assertions.assertEquals(usernameNotFoundException, exception.getMessage());
    }
    
}
