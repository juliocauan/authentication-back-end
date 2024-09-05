package br.com.juliocauan.authentication.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredResetTokenException;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.repository.specification.PasswordResetSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;

    @Transactional(noRollbackFor = ExpiredResetTokenException.class, readOnly = true)
    public PasswordReset findByToken(String token) {
        PasswordReset passwordReset = passwordResetRepository.findOne(PasswordResetSpecification.tokenEquals(token))
                .orElseThrow(() -> new EntityNotFoundException("Token [%s] not found!".formatted(token)));
        if (passwordReset.isExpired()) {
            passwordResetRepository.delete(passwordReset);
            throw new ExpiredResetTokenException("Expired Token!");
        }
        return passwordReset;
    }

    public PasswordReset register(User user) {
        PasswordReset passwordReset;
        Optional<PasswordReset> fetchedPasswordReset = passwordResetRepository.findOne(PasswordResetSpecification.userEquals(user));
        
        if (fetchedPasswordReset.isPresent()) {
            passwordReset = fetchedPasswordReset.get();
            passwordReset.update();
        } else {
            passwordReset = new PasswordReset(user);
        }

        return passwordResetRepository.save(passwordReset);
    }

    public void delete(PasswordReset passwordReset) {
        passwordResetRepository.deleteById(passwordReset.getId());
    }

}
