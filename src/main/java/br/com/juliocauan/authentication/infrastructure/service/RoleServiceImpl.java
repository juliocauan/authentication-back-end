package br.com.juliocauan.authentication.infrastructure.service;

import org.openapitools.model.EnumRole;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.service.RoleService;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;
    
    @Override
    public Role findByName(EnumRole name) {
        return roleRepository.findByName(name)
            .orElseThrow(() -> new EntityNotFoundException("Role Not Found with name: " + name));
    }
    
}
