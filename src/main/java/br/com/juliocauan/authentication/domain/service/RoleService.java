package br.com.juliocauan.authentication.domain.service;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

public abstract class RoleService {

        protected abstract RoleRepository getRepository();

        public final Role getByName(String name) {
                return getRepository().getByName(name)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                String.format("Role [%s] not found!", name)));
        }

        public final List<Role> getAll(String nameContains) {
                return getRepository().getAll(nameContains);
        }

        public final void register(String role) {
                if (getRepository().getByName(role).isPresent())
                        throw new EntityExistsException(String.format("Role [%s] already exists!", role));
                getRepository().register(role);
        }

        public final void delete(Role role) {
                if(role.getName().equals("ADMIN"))
                        throw new BadCredentialsException("You can not delete [ADMIN] role!");
                getRepository().delete(role);
        }

}
