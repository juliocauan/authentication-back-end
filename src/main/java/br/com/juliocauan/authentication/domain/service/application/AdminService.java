package br.com.juliocauan.authentication.domain.service.application;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.data.domain.Pageable;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.AdminException;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.UserMapper;

public abstract class AdminService {

    protected abstract UserRepository getUserRepository();

    protected abstract RoleRepository getRoleRepository();

    protected abstract String getLoggedUsername();

    public final List<UserInfo> getUserInfos(String usernameContains, String role, Pageable pageable) {
        return getUserRepository().findAllByFilters(usernameContains, role, pageable).stream()
                .map(UserMapper::domainToUserInfo)
                .collect(Collectors.toList());
    }

    public final void updateUserRoles(String username, Set<String> newRoles) {
        validateSelf(username);
        Set<Role> roles = newRoles.stream()
                .map(getRoleRepository()::findByName)
                .collect(Collectors.toSet());
        getUserRepository().updateUserRoles(username, roles);
    }

    private final void validateSelf(String username) {
        if (getLoggedUsername().equals(username))
            throw new AdminException("You can not update/delete your own account here!");
    }

    public final void deleteUser(String username) {
        validateSelf(username);
        getUserRepository().deleteByUsername(username);
    }

    public final List<String> getAllRoles(String nameContains) {
        return getRoleRepository().findAllByFilters(nameContains).stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    public final void registerRole(String role) {
        getRoleRepository().register(role);
    }

    public final void deleteRole(String roleName) {
        if (roleName.equals("ADMIN"))
            throw new AdminException("Role [ADMIN] can not be deleted!");
        Role role = getRoleRepository().findByName(roleName);
        List<User> users = getUserRepository().findAllByRole(role.getName());
        users.forEach(user -> {
            Set<Role> roles = user.getRoles();
            roles.remove(role);
            getUserRepository().updateUserRoles(user.getUsername(), roles);
        });
        getRoleRepository().delete(role);
    }

}
