package br.com.juliocauan.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocauan.authentication.domain.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;

public interface RoleRepositoryImpl extends RoleRepository, JpaRepository<RoleEntity, Short> {
    
}
