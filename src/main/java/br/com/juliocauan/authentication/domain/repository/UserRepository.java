package br.com.juliocauan.authentication.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserRepository {
    Optional<User> getByUsername(String username);
    List<User> getAll(String usernameContains, String roleName, Pageable pageable);
    List<User> getAll(String roleName);
    void register(User user);
    void delete(User user);
}
