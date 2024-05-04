package br.com.juliocauan.authentication.infrastructure.service.application;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.AdminService;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AdminServiceImpl extends AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    protected final UserRepository getUserRepository() {
        return userRepository;
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
