package br.com.juliocauan.authentication.domain.service.application;

import java.util.List;
import java.util.Set;

import org.openapitools.model.UserInfo;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.domain.service.UserService;

public abstract class AdminService {

    protected abstract UserService getUserService();
    protected abstract RoleService getRoleService();
    protected abstract String getLoggedUsername();

    public final List<UserInfo> getUserInfos(String usernameContains, String role) {
        return getUserService().getUserInfos(usernameContains, role);
    }

    public final void updateUserRoles(String username, Set<String> newRoles) {
        getUserService().updateRoles(username, newRoles);
    }

    public final void deleteUser(String username) {
        getUserService().delete(username);
    }

    public final List<String> getAllRoles(String nameContains) {
        return getRoleService().getAll(nameContains);
    }

    public final void registerRole(String role) {
        getRoleService().register(role);
    }

    public final void deleteRole(String roleName) {
      Role role = getRoleService().getByName(roleName);
      List<UserInfo> users = getUserService().getUserInfos(null, role.getName());
      users.forEach(user -> {
          Set<String> roles = user.getRoles();
          roles.remove(role.getName());
          getUserService().updateRoles(user.getUsername(), roles);
      });
      getRoleService().delete(role);
    }
    
}
