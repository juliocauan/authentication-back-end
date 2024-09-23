package br.com.juliomariano.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliomariano.authentication.domain.model.PasswordReset;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer>, JpaSpecificationExecutor<PasswordReset> { }
