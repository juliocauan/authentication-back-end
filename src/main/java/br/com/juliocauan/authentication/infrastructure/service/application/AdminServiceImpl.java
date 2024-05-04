package br.com.juliocauan.authentication.infrastructure.service.application;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.domain.service.application.AdminService;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AdminServiceImpl extends AdminService {

    private final UserServiceImpl userService;
    private final RoleRepository roleRepository;

    @Override
    protected final UserService getUserService() {
        return userService;
    }

    @Override
    protected final RoleRepository getRoleRepository() {
        return roleRepository;
    }

    @Override
    protected final String getLoggedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
