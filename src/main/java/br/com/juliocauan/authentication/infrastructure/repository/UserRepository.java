package br.com.juliocauan.authentication.infrastructure.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.util.PasswordUtil;
import jakarta.persistence.EntityExistsException;

import static br.com.juliocauan.authentication.infrastructure.repository.specification.UserSpecification.*;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    default User findByUsername(String username) {
        User user = this.findOne(Specification.where(usernameEquals(username)))
            .orElseThrow(() -> new UsernameNotFoundException("Username [%s] not found!".formatted(username)));
        return user;
    }

    default List<User> findAllByFilters(String usernameContains, String roleName, Pageable pageable) {
        return this.findAll(Specification
                .where(usernameContains(usernameContains)
                .and(hasRole(roleName))),
                pageable)
            .stream().collect(Collectors.toList());
    }

    default List<User> findAllByRole(String roleName) {
        return this.findAll(Specification
                .where(hasRole(roleName)))
            .stream().collect(Collectors.toList());
    }

    default void register(User user) {
        boolean userExists = this.exists(Specification.where(usernameEquals(user.getUsername())));
        if(userExists)
            throw new EntityExistsException("Username [%s] is already taken!".formatted(user.getUsername()));
        PasswordUtil.validateSecurity(user.getPassword());
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        this.save(user);
    }

    //TODO refatorar
    default void updateRole(User user) {
        this.save(user);
    }

    default void updatePassword(User user, String newPassword) {
        PasswordUtil.validateSecurity(newPassword);
        user.setPassword(PasswordUtil.encode(newPassword));
        this.save(user);
    }

    default void delete(String username) {
        User user = this.findByUsername(username);
        this.delete(user);
    }

}
