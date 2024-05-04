package br.com.juliocauan.authentication.domain.service.application;

import java.util.Collections;
import java.util.Set;

import org.openapitools.model.UserData;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;

public abstract class AuthenticationService {

    protected abstract UserRepository getUserRepository();

    protected abstract RoleRepository getRoleRepository();

    public abstract UserData authenticate(String username, String password);

    public final void registerUser(String username, String password) {
        getUserRepository().register(new User(username, password));
    }

    public final void registerAdmin(String username, String password) {
        User admin = new User(username, password);
        admin.setRoles(adminSet());
        getUserRepository().register(admin);
    }

    private final Set<Role> adminSet() {
        return Collections.singleton(getRoleRepository().findByName("ADMIN"));
    }
}
