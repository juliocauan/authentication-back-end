package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserRepository {
    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
