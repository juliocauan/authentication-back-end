package br.com.juliocauan.authentication.infrastructure.repository;

import static br.com.juliocauan.authentication.infrastructure.repository.specification.RoleSpecification.nameContains;
import static br.com.juliocauan.authentication.infrastructure.repository.specification.RoleSpecification.nameEquals;

import java.util.List;
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
            .orElseThrow(() -> new EntityNotFoundException(String.format("Role [%s] not found!", name)));
        return role;
    }

    default List<Role> findAllWithFilters(String nameContains) {
        return this.findAll(Specification.where(nameContains(nameContains)))
            .stream().collect(Collectors.toList());
    }

    default void register(String name) {
        boolean roleExists = this.exists(Specification.where(nameEquals(name)));
        if(roleExists)
            throw new EntityExistsException(String.format("Role [%s] already exists!", name));
        this.save(new Role(name));
    }

    default void delete(Role role) {
        this.deleteById(role.getId());
    }
    
}
