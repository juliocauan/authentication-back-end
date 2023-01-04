package br.com.juliocauan.authentication.infrastructure.repository;

import java.util.Optional;

import org.openapitools.model.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Short> {
    Optional<Role> findByName(EnumRole name);
}
