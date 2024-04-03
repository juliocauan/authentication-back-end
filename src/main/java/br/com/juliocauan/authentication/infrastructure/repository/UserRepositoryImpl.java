package br.com.juliocauan.authentication.infrastructure.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.repository.specification.UserSpecification;

public interface UserRepositoryImpl extends UserRepository, JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    @Override
    default List<User> getAll(String usernameContains, String roleName, Pageable pageable) {
        return this.findAll(Specification
            .where(UserSpecification.usernameContains(usernameContains)
            .and(UserSpecification.role(roleName))),
            pageable)
            .stream().collect(Collectors.toList());
    }

    @Override
    default List<User> getAll(String roleName) {
        return this.findAll(Specification
            .where(UserSpecification.role(roleName)))
            .stream().collect(Collectors.toList());
    }

    @Override
    default void register(User user) {
        this.save(user);
    }

    @Override
    default void delete(User user) {
        this.deleteById(user.getId());
    }

}
