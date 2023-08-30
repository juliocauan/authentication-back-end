package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.ResetPasswordToken;

public interface ResetPasswordTokenRepository {
    Optional<ResetPasswordToken> findByToken(String token);
}
