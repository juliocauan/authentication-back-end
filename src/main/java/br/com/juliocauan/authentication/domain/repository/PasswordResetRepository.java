package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;

public interface PasswordResetRepository {
    Optional<PasswordReset> getByToken(String token);
    Optional<PasswordReset> getByUser(User user);
    PasswordReset register(User user);
    void delete(PasswordReset passwordResetToken);
}
