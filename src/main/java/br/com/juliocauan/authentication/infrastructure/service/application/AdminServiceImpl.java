package br.com.juliocauan.authentication.infrastructure.service.application;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.AlterUserRolesForm;
import org.openapitools.model.EnumRole;
import org.openapitools.model.UserInfo;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.application.AdminService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminServiceImpl extends AdminService {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
    
    @Override
    public AlterUserRolesForm alterUserRole(AlterUserRolesForm alterUserRolesForm) {
        UserEntity user = UserMapper.domainToEntity(userService.getByUsername(alterUserRolesForm.getUsername()));
        Set<Role> roles = alterUserRolesForm.getRoles().stream().map(roleService::getByName).collect(Collectors.toSet());
        
        user.setRoles(RoleMapper.domainToEntity(roles));
        user = UserMapper.domainToEntity(userService.save(user));
        
        alterUserRolesForm
            .username(user.getUsername())
            .roles(RoleMapper.setRoleToSetEnumRole(user.getRoles()));
        return alterUserRolesForm;
    }

    @Override
    public List<UserInfo> getUserInfos(String username, EnumRole role) {
        List<User> users = userService.getAllUsers(username, role);
        return users.stream().map(UserMapper::domainToUserInfo).collect(Collectors.toList());
    }
    
}
