package br.com.juliocauan.authentication.application.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.AdminException;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.UserMapper;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<UserInfo> findAllUsers(String usernameContains, String role, Pageable pageable) {
        return userRepository.findAllByFilters(usernameContains, role, pageable).stream()
                .map(UserMapper::domainToUserInfo)
                .toList();
    }

    public void updateUserRoles(String username, Set<String> newRoles) {
        validateSelf(username);
        User user = userRepository.findByUsername(username);
        Set<Role> roles = newRoles.stream()
                .map(roleRepository::findByName)
                .collect(Collectors.toSet());
        userRepository.updateUserRoles(user, roles);
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
        User user = userRepository.findByUsername(username);
        user.setDisabled(true);
        userRepository.save(user);
    }

    public List<String> findAllRoles(String nameContains) {
        return roleRepository.findAllByFilters(nameContains).stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    public void registerRole(String role) {
        roleRepository.register(role);
    }

    public void deleteRole(String roleName) {
        if (roleName.equals("ADMIN"))
            throw new AdminException("Role [ADMIN] can not be deleted!");
        Role role = roleRepository.findByName(roleName);
        List<User> users = userRepository.findAllByRole(role.getName());
        users.forEach(user -> {
            Set<Role> roles = user.getRoles();
            roles.remove(role);
            userRepository.updateUserRoles(user, roles);
        });
        roleRepository.delete(role);
    }

}
