package br.com.juliomariano.authentication.application.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliomariano.authentication.domain.model.Role;
import br.com.juliomariano.authentication.domain.model.User;
import br.com.juliomariano.authentication.domain.service.RoleService;
import br.com.juliomariano.authentication.domain.service.UserService;
import br.com.juliomariano.authentication.infrastructure.exception.AdminException;
import br.com.juliomariano.authentication.util.mapper.UserMapper;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class AdminService {

    private final UserService userService;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public List<UserInfo> findAllUsers(String usernameContains, String role, Pageable pageable) {
        return userService.findAllByFilters(usernameContains, role, pageable).stream()
                .map(UserMapper.INSTANCE::toUserInfo)
                .toList();
    }

    public void updateUserRoles(String username, Set<String> newRoles) {
        validateSelf(username);
        User user = userService.findByUsername(username);
        Set<Role> roles = newRoles.stream()
                .map(roleService::findByName)
                .collect(Collectors.toSet());
        userService.updateUserRoles(user, roles);
    }

    private void validateSelf(String username) {
        if (getLoggedUsername().equals(username))
            throw new AdminException("You can not update/delete your own account here!");
    }

    private String getLoggedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void disableUser(String username) {
        validateSelf(username);
        userService.disable(username);
    }

    @Transactional(readOnly = true)
    public List<String> findAllRoles(String nameContains) {
        return roleService.findAllByFilters(nameContains).stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    public void registerRole(String role) {
        roleService.register(role);
    }

    public void deleteRole(String roleName) {
        if (roleName.equals("ADMIN"))
            throw new AdminException("Role [ADMIN] can not be deleted!");
        Role role = roleService.findByName(roleName);
        List<User> users = userService.findAllByRole(role.getName());
        users.forEach(user -> {
            Set<Role> roles = user.getRoles();
            roles.remove(role);
            userService.updateUserRoles(user, roles);
        });
        roleService.delete(role);
    }

}
