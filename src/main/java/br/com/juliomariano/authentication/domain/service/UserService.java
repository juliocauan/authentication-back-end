package br.com.juliomariano.authentication.domain.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliomariano.authentication.domain.model.Role;
import br.com.juliomariano.authentication.domain.model.User;
import br.com.juliomariano.authentication.infrastructure.repository.UserRepository;
import br.com.juliomariano.authentication.infrastructure.repository.specification.UserSpecification;
import br.com.juliomariano.authentication.util.PasswordUtil;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userRepository.findOne(UserSpecification.usernameEquals(username))
            .orElseThrow(() -> new UsernameNotFoundException("Username [%s] not found!".formatted(username)));
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAllByFilters(String usernameContains, String roleName, Pageable pageable) {
        return userRepository.findAll(Specification
                .where(UserSpecification.usernameContains(usernameContains)
                .and(UserSpecification.hasRole(roleName))),
                pageable)
            .stream().collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> findAllByRole(String roleName) {
        return userRepository.findAll(UserSpecification.hasRole(roleName))
            .stream().collect(Collectors.toList());
    }

    public void register(User user) {
        boolean userExists = userRepository.exists(UserSpecification.usernameEquals(user.getUsername()));
        if(userExists)
            throw new EntityExistsException("Username [%s] is already taken!".formatted(user.getUsername()));
        PasswordUtil.validateSecurity(user.getPassword());
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUserRoles(User user, Set<Role> newRoles) {
        user.setRoles(newRoles);
        userRepository.save(user);
    }

    public void updatePassword(User user, String newRawPassword) {
        PasswordUtil.validateSecurity(newRawPassword);
        user.setPassword(PasswordUtil.encode(newRawPassword));
        userRepository.save(user);
    }

    public void disable(String username) {
        User user = this.findByUsername(username);
        user.setDisabled(true);
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.deleteById(user.getId());
    }

}
