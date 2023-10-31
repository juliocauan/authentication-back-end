package br.com.juliocauan.authentication.infrastructure.service.application;

import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.AlterUserRolesForm;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.AdminService;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AdminServiceImpl extends AdminService {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
    
    @Override
    public final void updateUserRole(AlterUserRolesForm alterUserRolesForm) {
        UserEntity user = new UserEntity(userService.findByUsername(alterUserRolesForm.getUsername()));
        Set<RoleEntity> roles = alterUserRolesForm.getRoles().stream()
            .map(roleService::getByName)
            .map(RoleEntity::new)
            .collect(Collectors.toSet());
        
        user.setRoles(roles);
        userService.save(user);
    }
    
}
