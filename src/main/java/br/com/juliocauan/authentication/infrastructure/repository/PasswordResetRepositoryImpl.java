package br.com.juliocauan.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetEntity;

public interface PasswordResetRepositoryImpl extends PasswordResetRepository, JpaRepository<PasswordResetEntity, Integer> {

    @Override
    default void delete(PasswordReset passwordResetToken) {
        this.deleteById(passwordResetToken.getId());
    }

    @Override
    default PasswordReset register(User user) {
        return this.save(new PasswordResetEntity(user));
    }
    
}
