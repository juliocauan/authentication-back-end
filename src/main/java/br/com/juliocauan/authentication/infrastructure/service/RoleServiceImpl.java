package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class RoleServiceImpl extends RoleService {
    
    private final RoleRepositoryImpl roleRepository;

    @Override
    protected final RoleRepository getRepository() {
        return roleRepository;
    }

}
