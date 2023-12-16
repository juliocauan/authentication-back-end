package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class UserServiceImpl extends UserService {
    
    private final UserRepositoryImpl userRepository;
    private final RoleServiceImpl roleService;

    @Override
    protected final UserRepository getRepository() {
        return userRepository;
    }

    @Override
    protected final RoleService getRoleService() {
        return roleService;
    }
    
}
