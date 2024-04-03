package br.com.juliocauan.authentication.infrastructure.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.specification.RoleSpecification;

public interface RoleRepositoryImpl extends RoleRepository, JpaRepository<Role, Short>, JpaSpecificationExecutor<Role> {

    @Override
    default List<Role> getAll(String nameContains) {
        return this.findAll(Specification
            .where(RoleSpecification.nameContains(nameContains)))
            .stream().collect(Collectors.toList());
    }

    @Override
    default void register(String name) {
        this.save(new Role(name));
    }

    @Override
    default void delete(Role role) {
        this.deleteById(role.getId());
    }
    
}
