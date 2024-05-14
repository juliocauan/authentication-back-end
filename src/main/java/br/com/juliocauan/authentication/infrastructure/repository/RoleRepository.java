package br.com.juliocauan.authentication.infrastructure.repository;

import static br.com.juliocauan.authentication.infrastructure.repository.specification.RoleSpecification.nameContains;
import static br.com.juliocauan.authentication.infrastructure.repository.specification.RoleSpecification.nameEquals;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliocauan.authentication.domain.model.Role;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

public interface RoleRepository extends JpaRepository<Role, Short>, JpaSpecificationExecutor<Role> {

    default Role findByName(String name) {
        Role role = this.findOne(Specification.where(nameEquals(name)))
            .orElseThrow(() -> new EntityNotFoundException("Role [%s] not found!".formatted(name)));
        return role;
    }

    default Set<Role> findAllByFilters(String nameContains) {
        return this.findAll(Specification.where(nameContains(nameContains)))
            .stream().collect(Collectors.toSet());
    }

    default void register(String name) {
        boolean roleExists = this.exists(Specification.where(nameEquals(name)));
        if(roleExists)
            throw new EntityExistsException("Role [%s] already exists!".formatted(name));
        this.save(new Role(name));
    }
    
}
