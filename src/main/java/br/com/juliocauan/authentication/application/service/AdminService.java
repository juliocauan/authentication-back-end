package br.com.juliocauan.authentication.application.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.AdminException;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.UserMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    //TODO check
    public List<UserInfo> getUserInfos(String usernameContains, String role, Pageable pageable) {
        return userRepository.findAllByFilters(usernameContains, role, pageable).stream()
                .map(UserMapper::domainToUserInfo)
                .collect(Collectors.toList());
    }

    //TODO check
    public void updateUserRoles(String username, Set<String> newRoles) {
        validateSelf(username);
        Set<Role> roles = newRoles.stream()
                .map(roleRepository::findByName)
                .collect(Collectors.toSet());
        userRepository.updateUserRoles(username, roles);
    }

    //TODO check
    private void validateSelf(String username) {
        if (getLoggedUsername().equals(username))
            throw new AdminException("You can not update/delete your own account here!");
    }

    //TODO check
    private String getLoggedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    //TODO check
    public void deleteUser(String username) {
        validateSelf(username);
        userRepository.deleteByUsername(username);
    }

    //TODO check
    public List<String> getAllRoles(String nameContains) {
        return roleRepository.findAllByFilters(nameContains).stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    //TODO check
    public void registerRole(String role) {
        roleRepository.register(role);
    }

    //TODO check
    public void deleteRole(String roleName) {
        if (roleName.equals("ADMIN"))
            throw new AdminException("Role [ADMIN] can not be deleted!");
        Role role = roleRepository.findByName(roleName);
        List<User> users = userRepository.findAllByRole(role.getName());
        users.forEach(user -> {
            Set<Role> roles = user.getRoles();
            roles.remove(role);
            userRepository.updateUserRoles(user.getUsername(), roles);
        });
        roleRepository.delete(role);
    }

}
