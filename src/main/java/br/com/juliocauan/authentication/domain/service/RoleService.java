package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.domain.model.Role;

public interface RoleService {
	Role findByName(EnumRole name);
}
