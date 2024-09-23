package br.com.juliomariano.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliomariano.authentication.domain.model.Role;

public interface RoleRepository extends JpaRepository<Role, Short>, JpaSpecificationExecutor<Role> { }
