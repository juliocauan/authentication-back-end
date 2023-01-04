package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepositoryImpl roleRepository;

    @Override
    public RoleRepository getRepository() {
        return roleRepository;
    }
    
}
