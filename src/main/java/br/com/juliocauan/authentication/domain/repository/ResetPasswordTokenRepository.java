package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.ResetPasswordToken;
import br.com.juliocauan.authentication.domain.model.User;

public interface ResetPasswordTokenRepository {
    Optional<ResetPasswordToken> findByToken(String token);
    Optional<ResetPasswordToken> findByUser(User user);
}
