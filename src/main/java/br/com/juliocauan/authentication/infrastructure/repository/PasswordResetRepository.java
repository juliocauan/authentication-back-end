package br.com.juliocauan.authentication.infrastructure.repository;

import static br.com.juliocauan.authentication.infrastructure.repository.specification.PasswordResetSpecification.tokenEquals;
import static br.com.juliocauan.authentication.infrastructure.repository.specification.PasswordResetSpecification.userEquals;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredResetTokenException;
import jakarta.persistence.EntityNotFoundException;

//TODO REFACTOR
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer>, JpaSpecificationExecutor<PasswordReset> {

    default PasswordReset findByToken(String token) {
        PasswordReset passwordReset = this.findOne(Specification.where(tokenEquals(token)))
            .orElseThrow(() -> new EntityNotFoundException("Token [%s] not found!".formatted(token)));
        if(passwordReset.isExpired()) {
            this.delete(passwordReset);
            throw new ExpiredResetTokenException("Expired Token!");
        }
        return passwordReset;
    }

    default PasswordReset register(User user) {
        Optional<PasswordReset> oldPasswordReset = this.findOne(Specification.where(userEquals(user)));
        if(oldPasswordReset.isPresent())
            this.delete(oldPasswordReset.get());
        return this.save(new PasswordReset(user));
    }
    
}
