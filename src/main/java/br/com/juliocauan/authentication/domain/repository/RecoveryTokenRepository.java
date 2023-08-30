package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.RecoveryToken;
import br.com.juliocauan.authentication.domain.model.User;

public interface RecoveryTokenRepository {
    Optional<RecoveryToken> findByToken(String token);
    Optional<RecoveryToken> findByUser(User user);
}
