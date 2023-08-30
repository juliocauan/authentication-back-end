package br.com.juliocauan.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocauan.authentication.domain.repository.ResetPasswordTokenRepository;
import br.com.juliocauan.authentication.infrastructure.model.ResetPasswordTokenEntity;

public interface ResetPasswordTokenRepositoryImpl extends ResetPasswordTokenRepository, JpaRepository<ResetPasswordTokenEntity, Long> {
    
}
