package br.com.juliocauan.authentication.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.specification.RoleSpecification;

public interface RoleRepositoryImpl extends RoleRepository, JpaRepository<RoleEntity, Short>, JpaSpecificationExecutor<RoleEntity> {

    @Override
    default List<Role> getAll(String nameContains) {
        List<RoleEntity> roleEntities = findAll(Specification
            .where(RoleSpecification.nameContains(nameContains)));
        List<Role> roles = new ArrayList<>();
        roleEntities.forEach(roles::add);
        return roles;
    }
    
}
