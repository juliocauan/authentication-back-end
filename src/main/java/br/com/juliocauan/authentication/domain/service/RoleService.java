package br.com.juliocauan.authentication.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;

public abstract class RoleService {

        protected abstract RoleRepository getRepository();

        public final Role getByName(String name) {
                return getRepository().getByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Role Not Found with name: " + name));
        }

        public final List<String> getAllRoles(String contains) {
                return getRepository().getAllByRoleSubstring(contains).stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());
        }

        public abstract void save(String roleName);
        public abstract void delete(Role role);

}
