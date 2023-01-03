package br.com.juliocauan.authentication.domain.repository;

import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.domain.model.Role;

public interface RoleRepository {
	Role findByName(EnumRole name);
}
