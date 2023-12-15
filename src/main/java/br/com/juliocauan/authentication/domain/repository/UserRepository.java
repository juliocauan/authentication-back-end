package br.com.juliocauan.authentication.domain.repository;

import java.util.List;
import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserRepository {
    boolean existsByUsername(String username);
    Optional<User> getByUsername(String username);
    List<User> getAll(String usernameContains, String roleName);
    void register(User user);
    void delete(User user);
}
