package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserRepository {
    Boolean existsByAccessName(String accessName);
    Boolean existsByEmail(String email);
    Optional<User> findByAccessName(String accessName);
}
