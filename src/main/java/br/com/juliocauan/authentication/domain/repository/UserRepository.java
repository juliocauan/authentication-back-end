package br.com.juliocauan.authentication.domain.repository;

import java.util.List;
import java.util.Optional;

import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserRepository {
    boolean existsByUsername(String username);
    Optional<User> getByUsername(String username);
    List<User> getAllByUsernameSubstringAndRole(String username, EnumRole role);
}
