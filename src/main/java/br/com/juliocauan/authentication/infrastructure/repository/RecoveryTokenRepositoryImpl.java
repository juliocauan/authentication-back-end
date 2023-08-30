package br.com.juliocauan.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocauan.authentication.domain.repository.RecoveryTokenRepository;
import br.com.juliocauan.authentication.infrastructure.model.RecoveryTokenEntity;

public interface RecoveryTokenRepositoryImpl extends RecoveryTokenRepository, JpaRepository<RecoveryTokenEntity, Long> {
    
}
