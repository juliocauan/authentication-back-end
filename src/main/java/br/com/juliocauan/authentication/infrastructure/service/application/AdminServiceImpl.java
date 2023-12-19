package br.com.juliocauan.authentication.infrastructure.service.application;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.domain.service.application.AdminService;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AdminServiceImpl extends AdminService {
    
    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;

    @Override
    protected UserService getUserService() {
        return userService;
    }

    @Override
    protected RoleService getRoleService() {
        return roleService;
    }

    @Override
    protected final String getLoggedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
}
