package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.domain.model.Role;

public interface RoleRepository {
    Optional<Role> findByName(EnumRole name);
}
