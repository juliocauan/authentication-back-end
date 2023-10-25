package br.com.juliocauan.authentication.domain.repository;

import java.util.List;
import java.util.Optional;

import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserRepository {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    List<User> findAllByUsernameContainsAndRole(String username, EnumRole role);
}
