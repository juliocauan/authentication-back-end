package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;

public interface RoleService {

	RoleRepository getRepository();

	default Role getByName(EnumRole name) {
        return getRepository().findByName(name)
            .orElseThrow(() -> new EntityNotFoundException("Role Not Found with name: " + name));
    }

}
