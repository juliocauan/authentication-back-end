package br.com.juliocauan.authentication.domain.service.application;

import java.util.Collections;
import java.util.Set;

import org.openapitools.model.JWT;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.domain.service.UserService;

public abstract class AuthenticationService {

    protected abstract UserService getUserService();

    protected abstract RoleService getRoleService();

    public abstract JWT authenticate(String username, String password);

    public final void registerUser(String username, String password) {
        getUserService().register(User.newUser(username, password));
    }

    public final void registerAdmin(String username, String password) {
        User admin = User.newUser(username, password);
        admin = User.changeRoles(admin, adminSet());
        getUserService().register(admin);
    }

    private final Set<Role> adminSet() {
        return Collections.singleton(getRoleService().getByName("ADMIN"));
    }
}
