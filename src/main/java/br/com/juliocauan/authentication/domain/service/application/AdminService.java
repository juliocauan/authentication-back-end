package br.com.juliocauan.authentication.domain.service.application;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.exception.AdminException;
import br.com.juliocauan.authentication.util.UserMapper;

public abstract class AdminService {

    protected abstract UserService getUserService();

    protected abstract RoleService getRoleService();

    protected abstract String getLoggedUsername();

    public final List<UserInfo> getUserInfos(String usernameContains, String role) {
        return getUserService().getUsers(usernameContains, role).stream()
                .map(UserMapper::domainToUserInfo)
                .collect(Collectors.toList());
    }

    // TODO check this: consults database twice before being this method is called
    public final void updateUserRoles(String username, Set<String> newRoles) {
        User user = getUserService().getByUsername(username);
        Set<Role> roles = newRoles.stream()
                .map(getRoleService()::getByName)
                .collect(Collectors.toSet());

        user = User.changeRoles(user, roles);
        getUserService().update(user);
    }

    public final void deleteUser(String username) {
        if (getLoggedUsername().equals(username))
            throw new AdminException("You can not delete your own account here!");
        getUserService().delete(username);
    }

    public final List<String> getAllRoles(String nameContains) {
        return getRoleService().getAll(nameContains).stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    public final void registerRole(String role) {
        getRoleService().register(role);
    }

    public final void deleteRole(String roleName) {
        Role role = getRoleService().getByName(roleName);
        List<UserInfo> users = getUserInfos(null, role.getName());
        users.forEach(user -> {
            Set<String> roles = user.getRoles();
            roles.remove(role.getName());
            updateUserRoles(user.getUsername(), roles);
        });
        getRoleService().delete(role);
    }

}
