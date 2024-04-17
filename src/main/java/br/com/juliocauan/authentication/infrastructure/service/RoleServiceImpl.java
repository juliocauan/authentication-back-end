package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class RoleServiceImpl extends RoleService {
    
    private final RoleRepository roleRepository;

    @Override
    protected final RoleRepository getRepository() {
        return roleRepository;
    }

}
