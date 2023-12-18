package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.model.User;

public interface PasswordResetTokenRepository {
    Optional<PasswordResetToken> getByToken(String token);
    Optional<PasswordResetToken> getByUser(User user);
    void delete(PasswordResetToken passwordResetToken);
}
