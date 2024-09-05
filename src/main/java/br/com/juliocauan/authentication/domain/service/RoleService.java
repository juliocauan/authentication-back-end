package br.com.juliocauan.authentication.domain.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.specification.RoleSpecification;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Role findByName(String name) {
        Role role = roleRepository.findOne(RoleSpecification.nameEquals(name))
            .orElseThrow(() -> new EntityNotFoundException("Role [%s] not found!".formatted(name)));
        return role;
    }

    @Transactional(readOnly = true)
    public Set<Role> findAllByFilters(String nameContains) {
        return roleRepository.findAll(RoleSpecification.nameContains(nameContains))
            .stream().collect(Collectors.toSet());
    }

    public void register(String name) {
        boolean roleExists = roleRepository.exists(RoleSpecification.nameEquals(name));
        if(roleExists)
            throw new EntityExistsException("Role [%s] already exists!".formatted(name));
        roleRepository.save(new Role(name));
    }

    public void delete(Role role) {
        roleRepository.deleteById(role.getId());
    }
    
}
