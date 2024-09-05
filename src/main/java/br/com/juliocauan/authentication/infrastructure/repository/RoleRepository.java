package br.com.juliocauan.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliocauan.authentication.domain.model.Role;

public interface RoleRepository extends JpaRepository<Role, Short>, JpaSpecificationExecutor<Role> { }
