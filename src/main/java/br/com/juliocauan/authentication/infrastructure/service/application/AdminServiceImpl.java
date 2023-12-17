package br.com.juliocauan.authentication.infrastructure.service.application;

import java.util.List;
import java.util.Set;

import org.openapitools.model.UserInfo;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.service.application.AdminService;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AdminServiceImpl implements AdminService {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;

    @Override
    public final void delete(String roleName) {
        Role role = roleService.getByName(roleName);
        List<UserInfo> users = userService.getUserInfos(null, role.getName());
        users.forEach(user -> {
            Set<String> roles = user.getRoles();
            roles.remove(role.getName());
            userService.updateRoles(user.getUsername(), roles);
        });
        roleService.delete(role);
    }
}
