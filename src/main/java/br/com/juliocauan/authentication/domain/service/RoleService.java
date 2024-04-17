package br.com.juliocauan.authentication.domain.service;

import java.util.List;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;

public abstract class RoleService {

        protected abstract RoleRepository getRepository();

        public final Role getByName(String name) {
                return getRepository().findByName(name);
        }

        public final List<Role> getAll(String nameContains) {
                return getRepository().findAllByFilters(nameContains);
        }

        public final void register(String role) {
                getRepository().register(role);
        }

        public final void delete(Role role) {
                getRepository().delete(role);
        }

}
