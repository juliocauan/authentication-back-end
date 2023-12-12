package br.com.juliocauan.authentication.domain.service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;

public abstract class RoleService {

	protected abstract RoleRepository getRepository();

	public final Role getByName(String name) {
        return getRepository().getByName(name)
            .orElseThrow(() -> new EntityNotFoundException("Role Not Found with name: " + name));
    }

    public abstract void save(String role);

}
